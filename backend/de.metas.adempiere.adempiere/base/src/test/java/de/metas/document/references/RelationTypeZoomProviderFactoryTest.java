package de.metas.document.references;

import static org.adempiere.model.InterfaceWrapperHelper.newInstance;
import static org.adempiere.model.InterfaceWrapperHelper.save;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Map.Entry;
import org.adempiere.ad.service.ILookupDAO;
import org.adempiere.ad.service.impl.LookupDAO;
import org.adempiere.ad.service.impl.LookupDAO.TableRefInfo;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.test.AdempiereTestHelper;
import org.compiere.model.I_AD_Column;
import org.compiere.model.I_AD_Ref_Table;
import org.compiere.model.I_AD_Reference;
import org.compiere.model.I_AD_RelationType;
import org.compiere.model.I_AD_Table;
import org.compiere.model.X_AD_Reference;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;

import de.metas.util.Services;
import lombok.NonNull;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2017 metas GmbH
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

public class RelationTypeZoomProviderFactoryTest
{
	@Before
	public void init()
	{
		AdempiereTestHelper.get().init();
	}

	@Test
	public void findZoomProvider_IsTableRecordIdTarget_NoSource()
	{
		final String refTargetName = "RefTargetName1";
		final String validationType = X_AD_Reference.VALIDATIONTYPE_TableValidation;
		final I_AD_Reference referenceTarget = createReferenceSourceOrTarget(refTargetName, validationType);

		final String tableName = "TableName";
		final I_AD_Table table = createTable(tableName);

		final String keyColumnName = "TableName_ID";
		createColumn(table, keyColumnName);

		final String recordColumnname = "Record_ID";
		createColumn(table, recordColumnname);
		createRefTable(referenceTarget, table);

		final boolean isTableRecordIdTarget = true;
		final I_AD_RelationType relationType = createRelationType(isTableRecordIdTarget, null, referenceTarget);

		final TableRefInfo targetTableRefInfo = LookupDAO.TableRefInfo.builder()
				.setIdentifier(refTargetName)
				.setTableName(tableName)
				.setKeyColumn(keyColumnName)
				.setDisplayColumn(keyColumnName)
				.setValueDisplayed(true)
				.setDisplayColumnSQL("")
				.setTranslated(true)
				.setWhereClause("")
				.setOrderByClause("")
				.setZoomSO_Window_ID(null)
				.setZoomPO_Window_ID(null)
				.setZoomAD_Window_ID_Override(null)
				.setAutoComplete(false)
				.build();

		setupLookupDAOMock(ImmutableMap.of(
				referenceTarget.getAD_Reference_ID(), targetTableRefInfo));

		final RelationTypeZoomProvider zoomProvider = RelationTypeZoomProvidersFactory.findZoomProvider(relationType);

		assertThat(zoomProvider.isTableRecordIdTarget()).isTrue();
	}

	@Test
	public void findZoomProvider_Is_Not_TableRecordIdTarget_WithSource()
	{
		final String refTargetName = "RefTargetName1";
		final String validationType = X_AD_Reference.VALIDATIONTYPE_TableValidation;
		final I_AD_Reference referenceTarget = createReferenceSourceOrTarget(refTargetName, validationType);

		final String refSourceName = "RefSourceName1";
		final I_AD_Reference referenceSource = createReferenceSourceOrTarget(refSourceName, validationType);

		final String tableName = "TableName";
		final I_AD_Table table = createTable(tableName);

		final String keyColumnName = "TableName_ID";
		createColumn(table, keyColumnName);

		final String recordColumnname = "Record_ID";
		createColumn(table, recordColumnname);
		createRefTable(referenceTarget, table);

		final boolean isTableRecordIdTarget = false;

		final I_AD_RelationType relationType = createRelationType(isTableRecordIdTarget, referenceSource, referenceTarget);
		final TableRefInfo targetTableRefInfo = LookupDAO.TableRefInfo.builder()
				.setIdentifier(refTargetName)
				.setTableName(tableName)
				.setKeyColumn(keyColumnName)
				.setDisplayColumn(keyColumnName)
				.setValueDisplayed(true)
				.setDisplayColumnSQL("")
				.setTranslated(true)
				.setWhereClause("")
				.setOrderByClause("")
				.setZoomSO_Window_ID(null)
				.setZoomPO_Window_ID(null)
				.setZoomAD_Window_ID_Override(null)
				.setAutoComplete(false)
				.build();

		final TableRefInfo sourceTableRefInfo = LookupDAO.TableRefInfo.builder()
				.setIdentifier(refTargetName)
				.setTableName(tableName)
				.setKeyColumn(keyColumnName)
				.setDisplayColumn(keyColumnName)
				.setValueDisplayed(true)
				.setDisplayColumnSQL("")
				.setTranslated(true)
				.setWhereClause("")
				.setOrderByClause("")
				.setZoomSO_Window_ID(null)
				.setZoomPO_Window_ID(null)
				.setZoomAD_Window_ID_Override(null)
				.setAutoComplete(false)
				.build();

		setupLookupDAOMock(ImmutableMap.of(
				referenceTarget.getAD_Reference_ID(), targetTableRefInfo,
				referenceSource.getAD_Reference_ID(), sourceTableRefInfo));

		final RelationTypeZoomProvider zoomProvider = RelationTypeZoomProvidersFactory.findZoomProvider(relationType);

		assertThat(zoomProvider.isTableRecordIdTarget()).isFalse();
	}

	private void setupLookupDAOMock(Map<Integer, TableRefInfo> idToRefInfo)
	{
		final ILookupDAO lookupDao = Mockito.mock(ILookupDAO.class);
		Services.registerService(ILookupDAO.class, lookupDao);

		for (Entry<Integer, org.adempiere.ad.service.impl.LookupDAO.TableRefInfo> entry : idToRefInfo.entrySet())
		{
			Mockito.doReturn(entry.getValue()).when(lookupDao).retrieveTableRefInfo(entry.getKey());
		}
	}

	@Test(expected = AdempiereException.class)
	public void findZoomProvider_DefaultRelType_NoSource()
	{

		final String refTargetName = "RefTargetName1";
		final String validationType = X_AD_Reference.VALIDATIONTYPE_TableValidation;
		final I_AD_Reference referenceTarget = createReferenceSourceOrTarget(refTargetName, validationType);

		final String tableName = "TableName";
		final I_AD_Table table = createTable(tableName);

		final String keyColumnName = "TableName_ID";
		createColumn(table, keyColumnName);

		createRefTable(referenceTarget, table);

		final boolean isTableRecordIdTarget = false;
		final I_AD_RelationType relationType = createRelationType(isTableRecordIdTarget, null, referenceTarget);

		RelationTypeZoomProvidersFactory.findZoomProvider(relationType);

	}

	private I_AD_Column createColumn(I_AD_Table table, String columnname)
	{
		final I_AD_Column column = newInstance(I_AD_Column.class);
		column.setAD_Table_ID(table.getAD_Table_ID());
		column.setColumnName(columnname);
		save(column);

		return column;
	}

	private I_AD_Table createTable(String tableName)
	{
		final I_AD_Table table = newInstance(I_AD_Table.class);
		table.setName(tableName);
		table.setTableName(tableName);
		save(table);
		return table;
	}

	private I_AD_Ref_Table createRefTable(final I_AD_Reference reference, @NonNull final I_AD_Table table)
	{
		final I_AD_Ref_Table refTable = newInstance(I_AD_Ref_Table.class);
		refTable.setAD_Reference(reference);
		refTable.setAD_Table_ID(table.getAD_Table_ID());

		save(refTable);
		return refTable;
	}

	private I_AD_RelationType createRelationType(final boolean IsTableRecordIdTarget, final I_AD_Reference referenceSource, final I_AD_Reference referenceTarget)
	{
		final I_AD_RelationType relationType = newInstance(I_AD_RelationType.class);
		relationType.setIsTableRecordIdTarget(IsTableRecordIdTarget);
		relationType.setAD_Reference_Source(referenceSource);
		relationType.setAD_Reference_Target(referenceTarget);

		save(relationType);
		return relationType;
	}

	private I_AD_Reference createReferenceSourceOrTarget(final String name, final String validationType)
	{
		final I_AD_Reference reference = newInstance(I_AD_Reference.class);
		reference.setName(name);
		reference.setValidationType(validationType);

		save(reference);
		return reference;
	}
}
