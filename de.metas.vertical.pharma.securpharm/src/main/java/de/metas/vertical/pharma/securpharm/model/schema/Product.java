/*
 *
 *  * #%L
 *  * %%
 *  * Copyright (C) <current year> metas GmbH
 *  * %%
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as
 *  * published by the Free Software Foundation, either version 2 of the
 *  * License, or (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public
 *  * License along with this program. If not, see
 *  * <http://www.gnu.org/licenses/gpl-2.0.html>.
 *  * #L%
 *
 */

package de.metas.vertical.pharma.securpharm.model.schema;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class Product
{
	@NonNull
	@JsonProperty("pc")
	private String productCode;

	@NonNull
	@JsonProperty("lot")
	private String lot;

	@NonNull
	@JsonProperty("exp")
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyMMdd")
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyMM00")
	private String expirationDate;

	@JsonProperty("expNMVS")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate expirationDateNMVS;

	@JsonProperty("nhrn")
	private String pharmaNumber;

	@JsonProperty("info")
	private ProductInfo info;
}
