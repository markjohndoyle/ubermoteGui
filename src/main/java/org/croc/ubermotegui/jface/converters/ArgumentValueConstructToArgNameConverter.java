package org.croc.ubermotegui.jface.converters;

import org.croc.ubermote.model.ArgumentValueConstruct;
import org.eclipse.core.databinding.conversion.Converter;
import org.teleal.cling.model.meta.ActionArgument;

public class ArgumentValueConstructToArgNameConverter extends Converter {
	ActionArgument<?> arg;
	
	public ArgumentValueConstructToArgNameConverter() {
		super(ArgumentValueConstruct.class, String.class);
	}

	public ArgumentValueConstructToArgNameConverter(final Object fromType, final Object toType) {
		super(fromType, toType);
	}

	@Override
	public Object convert(Object arg0) {
		return ((ArgumentValueConstruct)arg0).getArgument().getName();
	}
}