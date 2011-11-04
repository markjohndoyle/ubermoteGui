package org.croc.ubermotegui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.croc.ubermote.model.ArgumentValueConstruct;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.ActionArgument.Direction;
import org.eclipse.swt.widgets.Text;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Point;

/**
 * @author mark
 * 
 */
public class ActionDialog extends Dialog {
	private DataBindingContext m_bindingContext;

	private final List<ArgumentValueConstruct> populatedArgs;
	private Table table;
	private TableViewer tableViewer;
	private TableViewerColumn tableViewerColumnValue;
	private Text text;
	private TableColumn tblclmnValue;

	public ActionDialog(Shell parentShell, Action<?> action) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM);

		List<ActionArgument> arguments = Arrays.asList(action.getArguments());
		this.populatedArgs = new ArrayList<ArgumentValueConstruct>();
		int count = 0;
		for (ActionArgument<?> aa : arguments) {
			this.populatedArgs.add(new ArgumentValueConstruct(aa, null));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, false));

		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		TableColumnLayout tcl_composite_1 = new TableColumnLayout();
		composite_1.setLayout(tcl_composite_1);

		tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn tableViewerColumnArgument = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnArgument = tableViewerColumnArgument.getColumn();
		tcl_composite_1.setColumnData(tblclmnArgument, new ColumnPixelData(150, true, true));
		tblclmnArgument.setText("argument");

		tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnValue = tableViewerColumnValue.getColumn();
		tcl_composite_1.setColumnData(tblclmnValue, new ColumnPixelData(150, true, true));
		tblclmnValue.setText("value");

		TableViewerColumn tableViewerColumnDatatype = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnDataType = tableViewerColumnDatatype.getColumn();
		tcl_composite_1.setColumnData(tblclmnDataType, new ColumnPixelData(150, true, true));
		tblclmnDataType.setText("data type");
		new Label(container, SWT.NONE);

		Label lblNewValue = new Label(container, SWT.NONE);
		lblNewValue.setText("Enter value");

		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m_bindingContext = initDataBindings();

		return parent;
	}

	/**
	 * @return the populatedArgs
	 */
	public List<ArgumentValueConstruct> getPopulatedArgs() {
		return populatedArgs;
	}

	protected Point getInitialSize() {
		return new Point(426, 252);
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		tableViewer.setContentProvider(contentProvider);
		//
		IObservableMap[] observeMaps = BeansObservables.observeMaps(contentProvider.getKnownElements(), ArgumentValueConstruct.class, new String[] {
				"argument", "value", "datatype" });
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		//
		WritableList writableList = new WritableList(populatedArgs, ArgumentValueConstruct.class);
		tableViewer.setInput(writableList);
		//
		IObservableValue textObserveTextObserveWidget = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue tableViewerObserveSingleSelection = ViewersObservables.observeSingleSelection(tableViewer);
		IObservableValue tableViewerValueObserveDetailValue = BeansObservables.observeDetailValue(tableViewerObserveSingleSelection,
				ArgumentValueConstruct.class, "value", Object.class);
		bindingContext.bindValue(textObserveTextObserveWidget, tableViewerValueObserveDetailValue, null, null);
		//
		return bindingContext;
	}
}
