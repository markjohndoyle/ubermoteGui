package org.croc.ubermotegui.control;

import org.croc.ubermote.UpnpScanner;
import org.croc.ubermotegui.commands.Command;
import org.croc.ubermotegui.commands.Play;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDAServiceId;

public class RemoteControl extends Dialog {

	private UpnpScanner upnpScanner;
	private RemoteDevice device;
	private Command playCommand;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public RemoteControl(Shell parentShell, UpnpScanner upnpScanner, RemoteDevice device) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM);

		this.upnpScanner = upnpScanner;
		this.device = device;
		initButtons();
	}

	private void initButtons() {
		Service service = device.findService(new UDAServiceId("AVTransport"));
		if (service != null) {
			Action playAction = service.getAction("Play");
			this.playCommand = new Play(this, playAction);
		}
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		// Component creation
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		// Component creation
		final Button btnPlay = new Button(container, SWT.NONE);
		btnPlay.addSelectionListener(new BtnPlaySelectionListener());
		btnPlay.setText("Play");

		return parent;
	}

	private class BtnPlaySelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (playCommand != null) {
				playCommand.execute();
			}
			else {
				System.err.println("Play not initialised");
			}
		}
	}

	public ControlPoint getControlPoint() {
		return this.upnpScanner.getUpnpService().getControlPoint();
	}
}
