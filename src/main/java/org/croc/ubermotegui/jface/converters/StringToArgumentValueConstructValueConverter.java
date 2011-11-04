package org.croc.ubermotegui.jface.converters;

import org.croc.ubermote.model.ArgumentValueConstruct;
import org.eclipse.core.databinding.conversion.Converter;
import org.teleal.cling.model.meta.ActionArgument;

public class StringToArgumentValueConstructValueConverter extends Converter {
	ActionArgument<?> arg;

	public StringToArgumentValueConstructValueConverter() {
		super(String.class, Object.class);
	}

	public StringToArgumentValueConstructValueConverter(ActionArgument<?> argument) {
		super(String.class, ArgumentValueConstruct.class);
		this.arg = argument;
	}

	@Override
	public Object convert(Object arg0) {
		System.out.println("converting " + arg0 + " to " + getToType().getClass().getName());
		String value = (String) arg0;
		return "not finished";
	}
}