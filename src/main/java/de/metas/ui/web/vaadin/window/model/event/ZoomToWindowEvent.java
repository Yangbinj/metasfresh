package de.metas.ui.web.vaadin.window.model.event;

import org.adempiere.model.ZoomInfoFactory.ZoomInfo;

import com.google.common.base.MoreObjects;

/*
 * #%L
 * metasfresh-webui
 * %%
 * Copyright (C) 2016 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

public class ZoomToWindowEvent extends ModelEvent
{
	public static ZoomToWindowEvent of(final Object model, final ZoomInfo zoomInfo)
	{
		return new ZoomToWindowEvent(model, zoomInfo);
	}

	private final ZoomInfo zoomInfo;

	private ZoomToWindowEvent(final Object model, final ZoomInfo zoomInfo)
	{
		super(model);
		this.zoomInfo = zoomInfo;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("zoomInfo", zoomInfo)
				.toString();
	}

	public int getWindowId()
	{
		return zoomInfo.getAD_Window_ID();
	}

}
