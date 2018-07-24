package de.metas.order.restart;

import static org.adempiere.model.InterfaceWrapperHelper.newInstance;
import static org.adempiere.model.InterfaceWrapperHelper.saveRecord;

import java.util.List;

import org.adempiere.ad.trx.api.ITrx;
import org.adempiere.exceptions.DBUniqueConstraintException;
import org.adempiere.model.CopyRecordFactory;
import org.adempiere.model.InterfaceWrapperHelper;
import org.adempiere.util.LegacyAdapters;
import org.adempiere.util.Services;
import org.adempiere.util.lang.IPair;
import org.adempiere.util.lang.ITableRecordReference;
import org.adempiere.util.lang.impl.TableRecordReference;
import org.compiere.model.I_C_Order;
import org.springframework.stereotype.Component;

import de.metas.document.engine.IDocument;
import de.metas.document.engine.IDocumentBL;
import lombok.NonNull;

/*
 * #%L
 * de.metas.business
 * %%
 * Copyright (C) 2018 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

/**
 * Note that there are other implementations to void "downstream" documents (currently C_Flatrate_Terms and C_Invoices).
 */
@Component
public class VoidOrderHandler implements VoidOrderWithRelatedDocsHandler
{

	@Override
	public RecordsToHandleKey getRecordsToHandleKey()
	{
		return RecordsToHandleKey.of(I_C_Order.Table_Name);
	}

	@Override
	public void handleOrderVoided(@NonNull final VoidOrderWithRelatedDocsRequest request)
	{
		final IDocumentBL documentBL = Services.get(IDocumentBL.class);

		final IPair<RecordsToHandleKey, List<ITableRecordReference>> recordsToHandle = request.getRecordsToHandle();

		final List<I_C_Order> orderRecordsToHandle = TableRecordReference.getModels(recordsToHandle.getRight(), I_C_Order.class);

		for (final I_C_Order orderRecord : orderRecordsToHandle)
		{
			// update the old orders' documentno
			final String documentNo = setVoidedOrderNewDocumenTNo(request.getVoidedOrderDocumentNoPrefix(), orderRecord, 1);

			final I_C_Order copiedOrderRecord = newInstance(I_C_Order.class);
			InterfaceWrapperHelper.copyValues(orderRecord, copiedOrderRecord);
			copiedOrderRecord.setDocumentNo(documentNo);
			saveRecord(copiedOrderRecord);

			// copy-with-details, set orderRecord's previous DocumentNo
			CopyRecordFactory
					.getCopyRecordSupport(I_C_Order.Table_Name)
					.setParentPO(LegacyAdapters.convertToPO(copiedOrderRecord))
					.setBase(true)
					.copyRecord(LegacyAdapters.convertToPO(orderRecord), ITrx.TRXNAME_ThreadInherited);

			documentBL.processEx(orderRecord, IDocument.ACTION_Void);
			saveRecord(orderRecord);
		}
	}

	private String setVoidedOrderNewDocumenTNo(
			@NonNull final String voidedOrderDocumentNoPrefix,
			@NonNull final I_C_Order orderRecord,
			int attemptCount)
	{
		final String prefixToUse;
		if (attemptCount <= 1)
		{
			prefixToUse = String.format("%s-", voidedOrderDocumentNoPrefix);
		}
		else
		{
			prefixToUse = String.format("%s(%s)-", voidedOrderDocumentNoPrefix, attemptCount);
		}

		final String documentNo = orderRecord.getDocumentNo();
		try
		{
			orderRecord.setDocumentNo(prefixToUse + documentNo);
			saveRecord(orderRecord);
		}
		catch (final DBUniqueConstraintException e)
		{
			orderRecord.setDocumentNo(documentNo); // go back to the original documentno for the next try, to avoid <prefix>(2)-<prefix>-document
			setVoidedOrderNewDocumenTNo(voidedOrderDocumentNoPrefix, orderRecord, attemptCount + 1);
		}
		return documentNo;
	}
}
