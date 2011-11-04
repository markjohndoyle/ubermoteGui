package org.croc.ubermotegui.jface.converters;

import org.croc.ubermote.model.ArgumentValueConstruct;
import org.eclipse.core.databinding.conversion.Converter;
import org.teleal.cling.model.meta.ActionArgument;

public class ArgumentValueConstructToValueConverter extends Converter {
	ActionArgument<?> arg;
	
	public ArgumentValueConstructToValueConverter() {
		super(ArgumentValueConstruct.class, String.class);
	}

	public ArgumentValueConstructToValueConverter(final Object fromType, final Object toType) {
		super(fromType, toType);
	}

	@Override
	public Object convert(Object arg0) {
		return ((ArgumentValueConstruct)arg0).getValue().toString();
	}
}