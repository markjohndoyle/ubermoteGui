package org.croc.ubermotegui;

import org.croc.ubermote.GenericActionInvocationCallback;
import org.croc.ubermote.UpnpScanner;
import org.croc.ubermote.model.ArgumentValueConstruct;
import org.croc.ubermote.model.DeviceModel;
import org.croc.ubermotegui.control.RemoteControl;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.ActionArgument.Direction;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteService;

public class MainApp {
	private DataBindingContext m_bindingContext;

	protected Shell shell;

	private DeviceModel model = new DeviceModel();
	private UpnpScanner upnpScanner = new UpnpScanner(model);
	private List listDevices;
	private ListViewer listViewerDevices;
	private ListViewer listViewerServices;
	private ListViewer listViewerActions;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
					window.open();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(797, 485);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		SashForm sashForm = new SashForm(composite_1, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		listViewerDevices = new ListViewer(sashForm, SWT.BORDER | SWT.V_SCROLL);
		listDevices = listViewerDevices.getList();

		Menu menu_1 = new Menu(listDevices);
		listDevices.setMenu(menu_1);

		MenuItem mntmDeviceDetails = new MenuItem(menu_1, SWT.NONE);
		mntmDeviceDetails.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) MainApp.this.listViewerDevices.getSelection();
				RemoteDevice selectedDevice = (RemoteDevice) selection.getFirstElement();
				String message = selectedDevice.getDetails().getFriendlyName() + " : " +
						selectedDevice.getIdentity().getDescriptorURL().getHost() + " : " + 
						selectedDevice.getDetails().getManufacturerDetails().getManufacturer()  + " : " +
						selectedDevice.getDetails().getModelDetails().getModelName()  + " : " +
						selectedDevice.getType().getDisplayString();
				MessageDialog detailsDialog = new MessageDialog(MainApp.this.shell, selectedDevice.getDisplayString(), null, message , MessageDialog.INFORMATION, new String[]{"OK"}, 0);
				detailsDialog.open();
			}
		});
		mntmDeviceDetails.setText("device details");

		listViewerServices = new ListViewer(sashForm, SWT.BORDER | SWT.V_SCROLL);
		List listService = listViewerServices.getList();
		
		listViewerActions = new ListViewer(sashForm, SWT.BORDER | SWT.V_SCROLL);
		listViewerActions.addDoubleClickListener(new IDoubleClickListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
			 */
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) MainApp.this.listViewerActions.getSelection();
				Action<?> selectedAction = (Action<?>) selection.getFirstElement();

				ActionDialog ad = new ActionDialog(MainApp.this.shell, selectedAction);
				int closeOption = ad.open();
				if(closeOption == Window.OK) {
					ActionInvocation<?> actionInvocation = new ActionInvocation(selectedAction);
					for(int i =0; i < ad.getPopulatedArgs().size(); i++) {
						ArgumentValueConstruct argVal = ad.getPopulatedArgs().get(i);
						if(argVal.getArgument().getDirection() == Direction.IN) {
							actionInvocation.setInput(argVal.getArgument().getName(), argVal.getValue());
						}
					}
					MainApp.this.upnpScanner.getUpnpService().getControlPoint().execute(new GenericActionInvocationCallback(actionInvocation));
				}
			}
		});
		List listActions = listViewerActions.getList();
		sashForm.setWeights(new int[] { 1, 1, 1 });

		Button scanButton = new Button(composite_1, SWT.NONE);
		scanButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MainApp.this.upnpScanner.scan();
			}
		});
		scanButton.setText("scan");
		// Component creation
		final Button btnRemote = new Button(composite_1, SWT.NONE);
		btnRemote.addSelectionListener(new BtnRemoteSelectionListener());
		btnRemote.setText("Remote");

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		m_bindingContext = initDataBindings();

	}
	/**
	 * @return
	 */
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		listViewerDevices.setContentProvider(listContentProvider);
		//
		IObservableMap observeMap = PojoObservables.observeMap(listContentProvider.getKnownElements(), RemoteDevice.class, "displayString");
		listViewerDevices.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		//
		IObservableList modelAvailableRemoteDevicesObserveList = BeansObservables.observeList(Realm.getDefault(), model, "availableRemoteDevices");
		listViewerDevices.setInput(modelAvailableRemoteDevicesObserveList);
		//
		ObservableListContentProvider listContentProvider_1 = new ObservableListContentProvider();
		listViewerServices.setContentProvider(listContentProvider_1);
		//
		IObservableMap observeMap_1 = PojoObservables.observeMap(listContentProvider_1.getKnownElements(), RemoteService.class, "serviceId");
		listViewerServices.setLabelProvider(new ObservableMapLabelProvider(observeMap_1));
		//
		IObservableValue listViewerDevicesObserveSingleSelection = ViewersObservables.observeSingleSelection(listViewerDevices);
		IObservableList listViewerDisplayStringObserveDetailList = PojoObservables.observeDetailList(listViewerDevicesObserveSingleSelection, "services", RemoteService.class);
		listViewerServices.setInput(listViewerDisplayStringObserveDetailList);
		//
		ObservableListContentProvider listContentProvider_2 = new ObservableListContentProvider();
		listViewerActions.setContentProvider(listContentProvider_2);
		//
		IObservableMap observeMap_2 = PojoObservables.observeMap(listContentProvider_2.getKnownElements(), Action.class, "name");
		listViewerActions.setLabelProvider(new ObservableMapLabelProvider(observeMap_2));
		//
		IObservableValue listViewerServicesObserveSingleSelection = ViewersObservables.observeSingleSelection(listViewerServices);
		IObservableList listViewerServicesDeviceObserveDetailList = PojoObservables.observeDetailList(listViewerServicesObserveSingleSelection, "actions", RemoteService.class);
		listViewerActions.setInput(listViewerServicesDeviceObserveDetailList);
		//
		return bindingContext;
	}
	
	
	private class BtnRemoteSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			IStructuredSelection selection = (IStructuredSelection)MainApp.this.listViewerDevices.getSelection();
			RemoteDevice selectedDevice = (RemoteDevice) selection.getFirstElement();
			new RemoteControl(MainApp.this.shell, MainApp.this.upnpScanner, selectedDevice).open();
		}
	}
}
