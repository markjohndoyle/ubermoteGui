package org.croc.ubermotegui.commands;

import org.croc.ubermotegui.control.RemoteControl;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.RemoteService;

public class Play implements Command {

	private Action<RemoteService> playAction;
	private RemoteControl control;

	public Play(RemoteControl control, Action<RemoteService> playAction) {
		this.control = control;
		this.playAction = playAction;
		
	}

	@Override
	public void execute() {
		ActionInvocation playActionInvocation = new ActionInvocation(playAction);
	
		playActionInvocation.setInput("InstanceID", "0");
		playActionInvocation.setInput("Speed", "1");
		
		ActionCallback playActionCallback = new ActionCallback(playActionInvocation) {

			@Override
			public void success(ActionInvocation invocation) {
				System.out.println("Success!");
			}

			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
				System.out.println(defaultMsg);
			}
		};
		
		control.getControlPoint().execute(playActionCallback);
	}

}
