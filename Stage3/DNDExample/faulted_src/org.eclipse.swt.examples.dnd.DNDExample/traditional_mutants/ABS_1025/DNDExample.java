// This is mutant program.
// Author : ysma

package org.eclipse.swt.examples.dnd;


import java.net.*;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class DNDExample
{

    private int dragOperation = 0;

    private org.eclipse.swt.dnd.Transfer[] dragTypes = new org.eclipse.swt.dnd.Transfer[0];

    private org.eclipse.swt.widgets.Control dragControl;

    private int dragControlType = 0;

    private org.eclipse.swt.dnd.DragSource dragSource;

    private java.lang.String dragDataText;

    private java.lang.String dragDataRTF;

    private java.lang.String dragDataHTML;

    private java.lang.String dragDataURL;

    private java.lang.String[] dragDataFiles;

    private org.eclipse.swt.widgets.List fileList;

    private boolean dragEnabled = false;

    private int dropOperation = 0;

    private int dropFeedback = 0;

    private int dropDefaultOperation = 0;

    private org.eclipse.swt.dnd.Transfer[] dropTypes = new org.eclipse.swt.dnd.Transfer[0];

    private org.eclipse.swt.dnd.DropTarget dropTarget;

    private org.eclipse.swt.widgets.Control dropControl;

    private int dropControlType = 0;

    private org.eclipse.swt.widgets.Composite defaultParent;

    private boolean dropEnabled = false;

    private org.eclipse.swt.widgets.Text dragConsole;

    private boolean dragEventDetail = false;

    private org.eclipse.swt.widgets.Text dropConsole;

    private boolean dropEventDetail = false;

    private org.eclipse.swt.graphics.Image itemImage;

    private static final int BUTTON_TOGGLE = 0;

    private static final int BUTTON_RADIO = 1;

    private static final int BUTTON_CHECK = 2;

    private static final int CANVAS = 3;

    private static final int LABEL = 4;

    private static final int LIST = 5;

    private static final int TABLE = 6;

    private static final int TREE = 7;

    private static final int TEXT = 8;

    private static final int STYLED_TEXT = 9;

    private static final int COMBO = 10;

    public static void main( java.lang.String[] args )
    {
        org.eclipse.swt.widgets.Display display = new org.eclipse.swt.widgets.Display();
        org.eclipse.swt.examples.dnd.DNDExample example = new org.eclipse.swt.examples.dnd.DNDExample();
        example.open( display );
        display.dispose();
    }

    private void addDragTransfer( org.eclipse.swt.dnd.Transfer transfer )
    {
        org.eclipse.swt.dnd.Transfer[] newTypes = new org.eclipse.swt.dnd.Transfer[dragTypes.length + 1];
        System.arraycopy( dragTypes, 0, newTypes, 0, dragTypes.length );
        newTypes[dragTypes.length] = transfer;
        dragTypes = newTypes;
        if (dragSource != null) {
            dragSource.setTransfer( dragTypes );
        }
    }

    private void addDropTransfer( org.eclipse.swt.dnd.Transfer transfer )
    {
        org.eclipse.swt.dnd.Transfer[] newTypes = new org.eclipse.swt.dnd.Transfer[dropTypes.length + 1];
        System.arraycopy( dropTypes, 0, newTypes, 0, dropTypes.length );
        newTypes[dropTypes.length] = transfer;
        dropTypes = newTypes;
        if (dropTarget != null) {
            dropTarget.setTransfer( dropTypes );
        }
    }

    private void createDragOperations( org.eclipse.swt.widgets.Composite parent )
    {
        parent.setLayout( new org.eclipse.swt.layout.RowLayout( SWT.VERTICAL ) );
        final org.eclipse.swt.widgets.Button moveButton = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        moveButton.setText( "DND.DROP_MOVE" );
        moveButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dragOperation |= DND.DROP_MOVE;
                } else {
                    dragOperation = dragOperation & ~DND.DROP_MOVE;
                    if (dragOperation == 0) {
                        dragOperation = DND.DROP_MOVE;
                        moveButton.setSelection( true );
                    }
                }
                if (dragEnabled) {
                    createDragSource();
                }
            }
        } );
        org.eclipse.swt.widgets.Button copyButton = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        copyButton.setText( "DND.DROP_COPY" );
        copyButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dragOperation |= DND.DROP_COPY;
                } else {
                    dragOperation = dragOperation & ~DND.DROP_COPY;
                    if (dragOperation == 0) {
                        dragOperation = DND.DROP_MOVE;
                        moveButton.setSelection( true );
                    }
                }
                if (dragEnabled) {
                    createDragSource();
                }
            }
        } );
        org.eclipse.swt.widgets.Button linkButton = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        linkButton.setText( "DND.DROP_LINK" );
        linkButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dragOperation |= DND.DROP_LINK;
                } else {
                    dragOperation = dragOperation & ~DND.DROP_LINK;
                    if (dragOperation == 0) {
                        dragOperation = DND.DROP_MOVE;
                        moveButton.setSelection( true );
                    }
                }
                if (dragEnabled) {
                    createDragSource();
                }
            }
        } );
        moveButton.setSelection( true );
        copyButton.setSelection( true );
        linkButton.setSelection( true );
        dragOperation |= DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
    }

    private void createDragSource()
    {
        if (dragSource != null) {
            dragSource.dispose();
        }
        dragSource = new org.eclipse.swt.dnd.DragSource( dragControl, dragOperation );
        dragSource.setTransfer( dragTypes );
        dragSource.addDragListener( new org.eclipse.swt.dnd.DragSourceListener(){
            public void dragFinished( org.eclipse.swt.dnd.DragSourceEvent event )
            {
                dragConsole.append( ">>dragFinished\n" );
                printEvent( event );
                dragDataText = dragDataRTF = dragDataHTML = dragDataURL = null;
                dragDataFiles = null;
                if (event.detail == DND.DROP_MOVE) {
                    switch (dragControlType) {
                    case BUTTON_CHECK :
                    case BUTTON_TOGGLE :
                    case BUTTON_RADIO :
                        {
                            org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) dragControl;
                            b.setText( "" );
                            break;
                        }

                    case STYLED_TEXT :
                        {
                            org.eclipse.swt.custom.StyledText text = (org.eclipse.swt.custom.StyledText) dragControl;
                            text.insert( "" );
                            break;
                        }

                    case TABLE :
                        {
                            org.eclipse.swt.widgets.Table table = (org.eclipse.swt.widgets.Table) dragControl;
                            org.eclipse.swt.widgets.TableItem[] items = table.getSelection();
                            for (int i = 0; i < items.length; i++) {
                                items[i].dispose();
                            }
                            break;
                        }

                    case TEXT :
                        {
                            org.eclipse.swt.widgets.Text text = (org.eclipse.swt.widgets.Text) dragControl;
                            text.insert( "" );
                            break;
                        }

                    case TREE :
                        {
                            org.eclipse.swt.widgets.Tree tree = (org.eclipse.swt.widgets.Tree) dragControl;
                            org.eclipse.swt.widgets.TreeItem[] items = tree.getSelection();
                            for (int i = 0; i < items.length; i++) {
                                items[i].dispose();
                            }
                            break;
                        }

                    case CANVAS :
                        {
                            dragControl.setData( "STRINGS", null );
                            dragControl.redraw();
                            break;
                        }

                    case LABEL :
                        {
                            org.eclipse.swt.widgets.Label label = (org.eclipse.swt.widgets.Label) dragControl;
                            label.setText( "" );
                            break;
                        }

                    case LIST :
                        {
                            org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) dragControl;
                            int[] indices = list.getSelectionIndices();
                            list.remove( indices );
                            break;
                        }

                    case COMBO :
                        {
                            org.eclipse.swt.widgets.Combo combo = (org.eclipse.swt.widgets.Combo) dragControl;
                            combo.setText( "" );
                            break;
                        }

                    }
                }
            }

            public void dragSetData( org.eclipse.swt.dnd.DragSourceEvent event )
            {
                dragConsole.append( ">>dragSetData\n" );
                printEvent( event );
                if (TextTransfer.getInstance().isSupportedType( event.dataType )) {
                    event.data = dragDataText;
                }
                if (RTFTransfer.getInstance().isSupportedType( event.dataType )) {
                    event.data = dragDataRTF;
                }
                if (HTMLTransfer.getInstance().isSupportedType( event.dataType )) {
                    event.data = dragDataHTML;
                }
                if (URLTransfer.getInstance().isSupportedType( event.dataType )) {
                    event.data = dragDataURL;
                }
                if (FileTransfer.getInstance().isSupportedType( event.dataType )) {
                    event.data = dragDataFiles;
                }
            }

            public void dragStart( org.eclipse.swt.dnd.DragSourceEvent event )
            {
                dragConsole.append( ">>dragStart\n" );
                printEvent( event );
                dragDataFiles = fileList.getItems();
                switch (dragControlType) {
                case BUTTON_CHECK :
                case BUTTON_TOGGLE :
                case BUTTON_RADIO :
                    {
                        org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) dragControl;
                        dragDataText = b.getSelection() ? "true" : "false";
                        break;
                    }

                case STYLED_TEXT :
                    {
                        org.eclipse.swt.custom.StyledText text = (org.eclipse.swt.custom.StyledText) dragControl;
                        java.lang.String s = text.getSelectionText();
                        if (s.length() == 0) {
                            event.doit = false;
                        } else {
                            dragDataText = s;
                        }
                        break;
                    }

                case TABLE :
                    {
                        org.eclipse.swt.widgets.Table table = (org.eclipse.swt.widgets.Table) dragControl;
                        org.eclipse.swt.widgets.TableItem[] items = table.getSelection();
                        if (items.length == 0) {
                            event.doit = false;
                        } else {
                            java.lang.StringBuffer buffer = new java.lang.StringBuffer();
                            for (int i = 0; i < items.length; i++) {
                                buffer.append( items[i].getText() );
                                if (items.length > 1 && i < items.length - 1) {
                                    buffer.append( "\n" );
                                }
                            }
                            dragDataText = buffer.toString();
                        }
                        break;
                    }

                case TEXT :
                    {
                        org.eclipse.swt.widgets.Text text = (org.eclipse.swt.widgets.Text) dragControl;
                        java.lang.String s = text.getSelectionText();
                        if (s.length() == 0) {
                            event.doit = false;
                        } else {
                            dragDataText = s;
                        }
                        break;
                    }

                case TREE :
                    {
                        org.eclipse.swt.widgets.Tree tree = (org.eclipse.swt.widgets.Tree) dragControl;
                        org.eclipse.swt.widgets.TreeItem[] items = tree.getSelection();
                        if (items.length == 0) {
                            event.doit = false;
                        } else {
                            java.lang.StringBuffer buffer = new java.lang.StringBuffer();
                            for (int i = 0; i < items.length; i++) {
                                buffer.append( items[i].getText() );
                                if (items.length > 1 && i < items.length - 1) {
                                    buffer.append( "\n" );
                                }
                            }
                            dragDataText = buffer.toString();
                        }
                        break;
                    }

                case CANVAS :
                    {
                        java.lang.String[] strings = (java.lang.String[]) dragControl.getData( "STRINGS" );
                        if (strings == null || strings.length == 0) {
                            event.doit = false;
                        } else {
                            java.lang.StringBuffer buffer = new java.lang.StringBuffer();
                            for (int i = 0; i < strings.length; i++) {
                                buffer.append( strings[i] );
                                if (strings.length > 1 && i < strings.length - 1) {
                                    buffer.append( "\n" );
                                }
                            }
                            dragDataText = buffer.toString();
                        }
                        break;
                    }

                case LABEL :
                    {
                        org.eclipse.swt.widgets.Label label = (org.eclipse.swt.widgets.Label) dragControl;
                        java.lang.String string = label.getText();
                        if (string.length() == 0) {
                            event.doit = false;
                        } else {
                            dragDataText = string;
                        }
                        break;
                    }

                case LIST :
                    {
                        org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) dragControl;
                        java.lang.String[] selection = list.getSelection();
                        if (selection.length == 0) {
                            event.doit = false;
                        } else {
                            java.lang.StringBuffer buffer = new java.lang.StringBuffer();
                            for (int i = 0; i < selection.length; i++) {
                                buffer.append( selection[i] );
                                if (selection.length > 1 && i < selection.length - 1) {
                                    buffer.append( "\n" );
                                }
                            }
                            dragDataText = buffer.toString();
                        }
                        break;
                    }

                case COMBO :
                    {
                        org.eclipse.swt.widgets.Combo combo = (org.eclipse.swt.widgets.Combo) dragControl;
                        java.lang.String string = combo.getText();
                        org.eclipse.swt.graphics.Point selection = combo.getSelection();
                        if (selection.x == selection.y) {
                            event.doit = false;
                        } else {
                            dragDataText = string.substring( selection.x, selection.y );
                        }
                        break;
                    }

                default  :
                    throw new org.eclipse.swt.SWTError( SWT.ERROR_NOT_IMPLEMENTED );

                }
                if (dragDataText != null) {
                    dragDataRTF = "{\\rtf1{\\colortbl;\\red255\\green0\\blue0;}\\cf1\\b " + dragDataText + "}";
                    dragDataHTML = "<b>" + dragDataText + "</b>";
                    dragDataURL = "http://" + dragDataText.replace( ' ', '.' );
                    try {
                        new java.net.URL( dragDataURL );
                    } catch ( java.net.MalformedURLException e ) {
                        dragDataURL = null;
                    }
                }
                for (int i = 0; i < dragTypes.length; i++) {
                    if (dragTypes[i] instanceof org.eclipse.swt.dnd.TextTransfer && dragDataText == null) {
                        event.doit = false;
                    }
                    if (dragTypes[i] instanceof org.eclipse.swt.dnd.RTFTransfer && dragDataRTF == null) {
                        event.doit = false;
                    }
                    if (dragTypes[i] instanceof org.eclipse.swt.dnd.HTMLTransfer && dragDataHTML == null) {
                        event.doit = false;
                    }
                    if (dragTypes[i] instanceof org.eclipse.swt.dnd.URLTransfer && dragDataURL == null) {
                        event.doit = false;
                    }
                    if (dragTypes[i] instanceof org.eclipse.swt.dnd.FileTransfer && (dragDataFiles == null || dragDataFiles.length == 0)) {
                        event.doit = false;
                    }
                }
            }
        } );
    }

    private void createDragTypes( org.eclipse.swt.widgets.Composite parent )
    {
        parent.setLayout( new org.eclipse.swt.layout.GridLayout() );
        org.eclipse.swt.widgets.Button textButton = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        textButton.setText( "Text Transfer" );
        textButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    addDragTransfer( TextTransfer.getInstance() );
                } else {
                    removeDragTransfer( TextTransfer.getInstance() );
                }
            }
        } );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "RTF Transfer" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    addDragTransfer( RTFTransfer.getInstance() );
                } else {
                    removeDragTransfer( RTFTransfer.getInstance() );
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "HTML Transfer" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    addDragTransfer( HTMLTransfer.getInstance() );
                } else {
                    removeDragTransfer( HTMLTransfer.getInstance() );
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "URL Transfer" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    addDragTransfer( URLTransfer.getInstance() );
                } else {
                    removeDragTransfer( URLTransfer.getInstance() );
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "File Transfer" );
        b.setLayoutData( new org.eclipse.swt.layout.GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    addDragTransfer( FileTransfer.getInstance() );
                } else {
                    removeDragTransfer( FileTransfer.getInstance() );
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.PUSH );
        b.setText( "Select File(s)" );
        b.setLayoutData( new org.eclipse.swt.layout.GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.FileDialog dialog = new org.eclipse.swt.widgets.FileDialog( fileList.getShell(), SWT.OPEN | SWT.MULTI );
                java.lang.String result = dialog.open();
                if (result != null && result.length() > 0) {
                    fileList.removeAll();
                    java.lang.String separator = System.getProperty( "file.separator" );
                    java.lang.String path = dialog.getFilterPath();
                    java.lang.String[] names = dialog.getFileNames();
                    for (int i = 0; i < names.length; i++) {
                        fileList.add( path + separator + names[i] );
                    }
                }
            }
        } );
        fileList = new org.eclipse.swt.widgets.List( parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
        org.eclipse.swt.layout.GridData data = new org.eclipse.swt.layout.GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        fileList.setLayoutData( data );
        textButton.setSelection( true );
        addDragTransfer( TextTransfer.getInstance() );
    }

    private void createDragWidget( org.eclipse.swt.widgets.Composite parent )
    {
        parent.setLayout( new org.eclipse.swt.layout.FormLayout() );
        org.eclipse.swt.widgets.Combo combo = new org.eclipse.swt.widgets.Combo( parent, SWT.READ_ONLY );
        combo.setItems( new java.lang.String[]{ "Toggle Button", "Radio Button", "Checkbox", "Canvas", "Label", "List", "Table", "Tree", "Text", "StyledText", "Combo" } );
        combo.select( LABEL );
        dragControlType = combo.getSelectionIndex();
        dragControl = createWidget( dragControlType, parent, "Drag Source" );
        combo.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                java.lang.Object data = dragControl.getLayoutData();
                org.eclipse.swt.widgets.Composite parent = dragControl.getParent();
                dragControl.dispose();
                org.eclipse.swt.widgets.Combo c = (org.eclipse.swt.widgets.Combo) e.widget;
                dragControlType = c.getSelectionIndex();
                dragControl = createWidget( dragControlType, parent, "Drag Source" );
                dragControl.setLayoutData( data );
                if (dragEnabled) {
                    createDragSource();
                }
                parent.layout();
            }
        } );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "DragSource" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                dragEnabled = b.getSelection();
                if (dragEnabled) {
                    createDragSource();
                } else {
                    if (dragSource != null) {
                        dragSource.dispose();
                    }
                    dragSource = null;
                }
            }
        } );
        b.setSelection( true );
        dragEnabled = true;
        org.eclipse.swt.layout.FormData data = new org.eclipse.swt.layout.FormData();
        data.top = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        data.bottom = new org.eclipse.swt.layout.FormAttachment( combo, -10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        data.right = new org.eclipse.swt.layout.FormAttachment( 100, -10 );
        dragControl.setLayoutData( data );
        data = new org.eclipse.swt.layout.FormData();
        data.bottom = new org.eclipse.swt.layout.FormAttachment( 100, -10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        combo.setLayoutData( data );
        data = new org.eclipse.swt.layout.FormData();
        data.bottom = new org.eclipse.swt.layout.FormAttachment( 100, -10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( combo, 10 );
        b.setLayoutData( data );
    }

    private void createDropOperations( org.eclipse.swt.widgets.Composite parent )
    {
        parent.setLayout( new org.eclipse.swt.layout.RowLayout( SWT.VERTICAL ) );
        final org.eclipse.swt.widgets.Button moveButton = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        moveButton.setText( "DND.DROP_MOVE" );
        moveButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropOperation |= DND.DROP_MOVE;
                } else {
                    dropOperation = dropOperation & ~DND.DROP_MOVE;
                    if (dropOperation == 0 || (dropDefaultOperation & DND.DROP_MOVE) != 0) {
                        dropOperation |= DND.DROP_MOVE;
                        moveButton.setSelection( true );
                    }
                }
                if (dropEnabled) {
                    createDropTarget();
                }
            }
        } );
        final org.eclipse.swt.widgets.Button copyButton = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        copyButton.setText( "DND.DROP_COPY" );
        copyButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropOperation |= DND.DROP_COPY;
                } else {
                    dropOperation = dropOperation & ~DND.DROP_COPY;
                    if (dropOperation == 0 || (dropDefaultOperation & DND.DROP_COPY) != 0) {
                        dropOperation = DND.DROP_COPY;
                        copyButton.setSelection( true );
                    }
                }
                if (dropEnabled) {
                    createDropTarget();
                }
            }
        } );
        final org.eclipse.swt.widgets.Button linkButton = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        linkButton.setText( "DND.DROP_LINK" );
        linkButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropOperation |= DND.DROP_LINK;
                } else {
                    dropOperation = dropOperation & ~DND.DROP_LINK;
                    if (dropOperation == 0 || (dropDefaultOperation & DND.DROP_LINK) != 0) {
                        dropOperation = DND.DROP_LINK;
                        linkButton.setSelection( true );
                    }
                }
                if (dropEnabled) {
                    createDropTarget();
                }
            }
        } );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "DND.DROP_DEFAULT" );
        defaultParent = new org.eclipse.swt.widgets.Composite( parent, SWT.NONE );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropOperation |= DND.DROP_DEFAULT;
                    defaultParent.setVisible( true );
                } else {
                    dropOperation = dropOperation & ~DND.DROP_DEFAULT;
                    defaultParent.setVisible( false );
                }
                if (dropEnabled) {
                    createDropTarget();
                }
            }
        } );
        defaultParent.setVisible( false );
        org.eclipse.swt.layout.GridLayout layout = new org.eclipse.swt.layout.GridLayout();
        layout.marginWidth = 20;
        defaultParent.setLayout( layout );
        org.eclipse.swt.widgets.Label label = new org.eclipse.swt.widgets.Label( defaultParent, SWT.NONE );
        label.setText( "Value for default operation is:" );
        b = new org.eclipse.swt.widgets.Button( defaultParent, SWT.RADIO );
        b.setText( "DND.DROP_MOVE" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropDefaultOperation = DND.DROP_MOVE;
                    dropOperation |= DND.DROP_MOVE;
                    moveButton.setSelection( true );
                    if (dropEnabled) {
                        createDropTarget();
                    }
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( defaultParent, SWT.RADIO );
        b.setText( "DND.DROP_COPY" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropDefaultOperation = DND.DROP_COPY;
                    dropOperation |= DND.DROP_COPY;
                    copyButton.setSelection( true );
                    if (dropEnabled) {
                        createDropTarget();
                    }
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( defaultParent, SWT.RADIO );
        b.setText( "DND.DROP_LINK" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropDefaultOperation = DND.DROP_LINK;
                    dropOperation |= DND.DROP_LINK;
                    linkButton.setSelection( true );
                    if (dropEnabled) {
                        createDropTarget();
                    }
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( defaultParent, SWT.RADIO );
        b.setText( "DND.DROP_NONE" );
        b.setSelection( true );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropDefaultOperation = DND.DROP_NONE;
                    dropOperation &= ~DND.DROP_DEFAULT;
                    if (dropEnabled) {
                        createDropTarget();
                    }
                }
            }
        } );
        moveButton.setSelection( true );
        copyButton.setSelection( true );
        linkButton.setSelection( true );
        dropOperation = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
    }

    private void createDropTarget()
    {
        if (dropTarget != null) {
            dropTarget.dispose();
        }
        dropTarget = new org.eclipse.swt.dnd.DropTarget( dropControl, dropOperation );
        dropTarget.setTransfer( dropTypes );
        dropTarget.addDropListener( new org.eclipse.swt.dnd.DropTargetListener(){
            public void dragEnter( org.eclipse.swt.dnd.DropTargetEvent event )
            {
                dropConsole.append( ">>dragEnter\n" );
                printEvent( event );
                if (event.detail == DND.DROP_DEFAULT) {
                    event.detail = dropDefaultOperation;
                }
                event.feedback = dropFeedback;
            }

            public void dragLeave( org.eclipse.swt.dnd.DropTargetEvent event )
            {
                dropConsole.append( ">>dragLeave\n" );
                printEvent( event );
            }

            public void dragOperationChanged( org.eclipse.swt.dnd.DropTargetEvent event )
            {
                dropConsole.append( ">>dragOperationChanged\n" );
                printEvent( event );
                if (event.detail == DND.DROP_DEFAULT) {
                    event.detail = dropDefaultOperation;
                }
                event.feedback = dropFeedback;
            }

            public void dragOver( org.eclipse.swt.dnd.DropTargetEvent event )
            {
                dropConsole.append( ">>dragOver\n" );
                printEvent( event );
                event.feedback = dropFeedback;
            }

            public void drop( org.eclipse.swt.dnd.DropTargetEvent event )
            {
                dropConsole.append( ">>drop\n" );
                printEvent( event );
                java.lang.String[] strings = null;
                if (TextTransfer.getInstance().isSupportedType( event.currentDataType ) || RTFTransfer.getInstance().isSupportedType( event.currentDataType ) || HTMLTransfer.getInstance().isSupportedType( event.currentDataType ) || URLTransfer.getInstance().isSupportedType( event.currentDataType )) {
                    strings = new java.lang.String[]{ (java.lang.String) event.data };
                }
                if (FileTransfer.getInstance().isSupportedType( event.currentDataType )) {
                    strings = (java.lang.String[]) event.data;
                }
                if (strings == null || strings.length == 0) {
                    dropConsole.append( "!!Invalid data dropped" );
                    return;
                }
                if (strings.length == 1 && (dropControlType == TABLE || dropControlType == TREE || dropControlType == LIST)) {
                    java.lang.String string = strings[0];
                    int count = 0;
                    int offset = string.indexOf( "\n", 0 );
                    while (offset > 0) {
                        count++;
                        offset = string.indexOf( "\n", offset + 1 );
                    }
                    if (count > 0) {
                        strings = new java.lang.String[count + 1];
                        int start = 0;
                        int end = string.indexOf( "\n" );
                        int index = 0;
                        while (start < end) {
                            strings[index++] = string.substring( start, end );
                            start = end + 1;
                            end = string.indexOf( "\n", start );
                            if (end == -1) {
                                end = string.length();
                            }
                        }
                    }
                }
                switch (dropControlType) {
                case BUTTON_CHECK :
                case BUTTON_TOGGLE :
                case BUTTON_RADIO :
                    {
                        org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) dropControl;
                        b.setText( strings[0] );
                        break;
                    }

                case STYLED_TEXT :
                    {
                        org.eclipse.swt.custom.StyledText text = (org.eclipse.swt.custom.StyledText) dropControl;
                        for (int i = 0; i < strings.length; i++) {
                            text.insert( strings[i] );
                        }
                        break;
                    }

                case TABLE :
                    {
                        org.eclipse.swt.widgets.Table table = (org.eclipse.swt.widgets.Table) dropControl;
                        org.eclipse.swt.graphics.Point p = event.display.map( null, table, event.x, event.y );
                        org.eclipse.swt.widgets.TableItem dropItem = table.getItem( p );
                        int index = dropItem == null ? table.getItemCount() : table.indexOf( dropItem );
                        for (int i = 0; i < strings.length; i++) {
                            org.eclipse.swt.widgets.TableItem item = new org.eclipse.swt.widgets.TableItem( table, SWT.NONE, index );
                            item.setText( 0, strings[i] );
                            item.setText( 1, "dropped item" );
                        }
                        org.eclipse.swt.widgets.TableColumn[] columns = table.getColumns();
                        for (int i = 0; i < columns.length; i++) {
                            columns[i].pack();
                        }
                        break;
                    }

                case TEXT :
                    {
                        org.eclipse.swt.widgets.Text text = (org.eclipse.swt.widgets.Text) dropControl;
                        for (int i = 0; i < strings.length; i++) {
                            text.append( strings[i] + "\n" );
                        }
                        break;
                    }

                case TREE :
                    {
                        org.eclipse.swt.widgets.Tree tree = (org.eclipse.swt.widgets.Tree) dropControl;
                        org.eclipse.swt.graphics.Point p = event.display.map( null, tree, event.x, event.y );
                        org.eclipse.swt.widgets.TreeItem parentItem = tree.getItem( p );
                        for (int i = 0; (-i) < strings.length; i++) {
                            org.eclipse.swt.widgets.TreeItem item = parentItem != null ? new org.eclipse.swt.widgets.TreeItem( parentItem, SWT.NONE ) : new org.eclipse.swt.widgets.TreeItem( tree, SWT.NONE );
                            item.setText( strings[i] );
                        }
                        break;
                    }

                case CANVAS :
                    {
                        dropControl.setData( "STRINGS", strings );
                        dropControl.redraw();
                        break;
                    }

                case LABEL :
                    {
                        org.eclipse.swt.widgets.Label label = (org.eclipse.swt.widgets.Label) dropControl;
                        label.setText( strings[0] );
                        break;
                    }

                case LIST :
                    {
                        org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) dropControl;
                        for (int i = 0; i < strings.length; i++) {
                            list.add( strings[i] );
                        }
                        break;
                    }

                case COMBO :
                    {
                        org.eclipse.swt.widgets.Combo combo = (org.eclipse.swt.widgets.Combo) dropControl;
                        combo.setText( strings[0] );
                        break;
                    }

                default  :
                    throw new org.eclipse.swt.SWTError( SWT.ERROR_NOT_IMPLEMENTED );

                }
            }

            public void dropAccept( org.eclipse.swt.dnd.DropTargetEvent event )
            {
                dropConsole.append( ">>dropAccept\n" );
                printEvent( event );
            }
        } );
    }

    private void createFeedbackTypes( org.eclipse.swt.widgets.Group parent )
    {
        parent.setLayout( new org.eclipse.swt.layout.RowLayout( SWT.VERTICAL ) );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "FEEDBACK_SELECT" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropFeedback |= DND.FEEDBACK_SELECT;
                } else {
                    dropFeedback &= ~DND.FEEDBACK_SELECT;
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "FEEDBACK_SCROLL" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropFeedback |= DND.FEEDBACK_SCROLL;
                } else {
                    dropFeedback &= ~DND.FEEDBACK_SCROLL;
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "FEEDBACK_INSERT_BEFORE" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropFeedback |= DND.FEEDBACK_INSERT_BEFORE;
                } else {
                    dropFeedback &= ~DND.FEEDBACK_INSERT_BEFORE;
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "FEEDBACK_INSERT_AFTER" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropFeedback |= DND.FEEDBACK_INSERT_AFTER;
                } else {
                    dropFeedback &= ~DND.FEEDBACK_INSERT_AFTER;
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "FEEDBACK_EXPAND" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    dropFeedback |= DND.FEEDBACK_EXPAND;
                } else {
                    dropFeedback &= ~DND.FEEDBACK_EXPAND;
                }
            }
        } );
    }

    private void createDropTypes( org.eclipse.swt.widgets.Composite parent )
    {
        parent.setLayout( new org.eclipse.swt.layout.RowLayout( SWT.VERTICAL ) );
        org.eclipse.swt.widgets.Button textButton = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        textButton.setText( "Text Transfer" );
        textButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    addDropTransfer( TextTransfer.getInstance() );
                } else {
                    removeDropTransfer( TextTransfer.getInstance() );
                }
            }
        } );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "RTF Transfer" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    addDropTransfer( RTFTransfer.getInstance() );
                } else {
                    removeDropTransfer( RTFTransfer.getInstance() );
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "HTML Transfer" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    addDropTransfer( HTMLTransfer.getInstance() );
                } else {
                    removeDropTransfer( HTMLTransfer.getInstance() );
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "URL Transfer" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    addDropTransfer( URLTransfer.getInstance() );
                } else {
                    removeDropTransfer( URLTransfer.getInstance() );
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "File Transfer" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                if (b.getSelection()) {
                    addDropTransfer( FileTransfer.getInstance() );
                } else {
                    removeDropTransfer( FileTransfer.getInstance() );
                }
            }
        } );
        textButton.setSelection( true );
        addDropTransfer( TextTransfer.getInstance() );
    }

    private void createDropWidget( org.eclipse.swt.widgets.Composite parent )
    {
        parent.setLayout( new org.eclipse.swt.layout.FormLayout() );
        org.eclipse.swt.widgets.Combo combo = new org.eclipse.swt.widgets.Combo( parent, SWT.READ_ONLY );
        combo.setItems( new java.lang.String[]{ "Toggle Button", "Radio Button", "Checkbox", "Canvas", "Label", "List", "Table", "Tree", "Text", "StyledText", "Combo" } );
        combo.select( LABEL );
        dropControlType = combo.getSelectionIndex();
        dropControl = createWidget( dropControlType, parent, "Drop Target" );
        combo.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                java.lang.Object data = dropControl.getLayoutData();
                org.eclipse.swt.widgets.Composite parent = dropControl.getParent();
                dropControl.dispose();
                org.eclipse.swt.widgets.Combo c = (org.eclipse.swt.widgets.Combo) e.widget;
                dropControlType = c.getSelectionIndex();
                dropControl = createWidget( dropControlType, parent, "Drop Target" );
                dropControl.setLayoutData( data );
                if (dropEnabled) {
                    createDropTarget();
                }
                parent.layout();
            }
        } );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
        b.setText( "DropTarget" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.Button b = (org.eclipse.swt.widgets.Button) e.widget;
                dropEnabled = b.getSelection();
                if (dropEnabled) {
                    createDropTarget();
                } else {
                    if (dropTarget != null) {
                        dropTarget.dispose();
                    }
                    dropTarget = null;
                }
            }
        } );
        b.setSelection( true );
        dropEnabled = true;
        org.eclipse.swt.layout.FormData data = new org.eclipse.swt.layout.FormData();
        data.top = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        data.bottom = new org.eclipse.swt.layout.FormAttachment( combo, -10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        data.right = new org.eclipse.swt.layout.FormAttachment( 100, -10 );
        dropControl.setLayoutData( data );
        data = new org.eclipse.swt.layout.FormData();
        data.bottom = new org.eclipse.swt.layout.FormAttachment( 100, -10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        combo.setLayoutData( data );
        data = new org.eclipse.swt.layout.FormData();
        data.bottom = new org.eclipse.swt.layout.FormAttachment( 100, -10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( combo, 10 );
        b.setLayoutData( data );
    }

    private org.eclipse.swt.widgets.Control createWidget( int type, org.eclipse.swt.widgets.Composite parent, java.lang.String prefix )
    {
        switch (type) {
        case BUTTON_CHECK :
            {
                org.eclipse.swt.widgets.Button button = new org.eclipse.swt.widgets.Button( parent, SWT.CHECK );
                button.setText( prefix + " Check box" );
                return button;
            }

        case BUTTON_TOGGLE :
            {
                org.eclipse.swt.widgets.Button button = new org.eclipse.swt.widgets.Button( parent, SWT.TOGGLE );
                button.setText( prefix + " Toggle button" );
                return button;
            }

        case BUTTON_RADIO :
            {
                org.eclipse.swt.widgets.Button button = new org.eclipse.swt.widgets.Button( parent, SWT.RADIO );
                button.setText( prefix + " Radio button" );
                return button;
            }

        case STYLED_TEXT :
            {
                org.eclipse.swt.custom.StyledText text = new org.eclipse.swt.custom.StyledText( parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
                text.setText( prefix + " Styled Text" );
                return text;
            }

        case TABLE :
            {
                org.eclipse.swt.widgets.Table table = new org.eclipse.swt.widgets.Table( parent, SWT.BORDER | SWT.MULTI );
                table.setHeaderVisible( true );
                org.eclipse.swt.widgets.TableColumn column0 = new org.eclipse.swt.widgets.TableColumn( table, SWT.LEFT );
                column0.setText( "Name" );
                org.eclipse.swt.widgets.TableColumn column1 = new org.eclipse.swt.widgets.TableColumn( table, SWT.RIGHT );
                column1.setText( "Value" );
                org.eclipse.swt.widgets.TableColumn column2 = new org.eclipse.swt.widgets.TableColumn( table, SWT.CENTER );
                column2.setText( "Description" );
                for (int i = 0; i < 10; i++) {
                    org.eclipse.swt.widgets.TableItem item = new org.eclipse.swt.widgets.TableItem( table, SWT.NONE );
                    item.setText( 0, prefix + " name " + i );
                    item.setText( 1, prefix + " value " + i );
                    item.setText( 2, prefix + " description " + i );
                    item.setImage( itemImage );
                }
                column0.pack();
                column1.pack();
                column2.pack();
                return table;
            }

        case TEXT :
            {
                org.eclipse.swt.widgets.Text text = new org.eclipse.swt.widgets.Text( parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
                text.setText( prefix + " Text" );
                return text;
            }

        case TREE :
            {
                org.eclipse.swt.widgets.Tree tree = new org.eclipse.swt.widgets.Tree( parent, SWT.BORDER | SWT.MULTI );
                tree.setHeaderVisible( true );
                org.eclipse.swt.widgets.TreeColumn column0 = new org.eclipse.swt.widgets.TreeColumn( tree, SWT.LEFT );
                column0.setText( "Name" );
                org.eclipse.swt.widgets.TreeColumn column1 = new org.eclipse.swt.widgets.TreeColumn( tree, SWT.RIGHT );
                column1.setText( "Value" );
                org.eclipse.swt.widgets.TreeColumn column2 = new org.eclipse.swt.widgets.TreeColumn( tree, SWT.CENTER );
                column2.setText( "Description" );
                for (int i = 0; i < 3; i++) {
                    org.eclipse.swt.widgets.TreeItem item = new org.eclipse.swt.widgets.TreeItem( tree, SWT.NONE );
                    item.setText( 0, prefix + " name " + i );
                    item.setText( 1, prefix + " value " + i );
                    item.setText( 2, prefix + " description " + i );
                    item.setImage( itemImage );
                    for (int j = 0; j < 3; j++) {
                        org.eclipse.swt.widgets.TreeItem subItem = new org.eclipse.swt.widgets.TreeItem( item, SWT.NONE );
                        subItem.setText( 0, prefix + " name " + i + " " + j );
                        subItem.setText( 1, prefix + " value " + i + " " + j );
                        subItem.setText( 2, prefix + " description " + i + " " + j );
                        subItem.setImage( itemImage );
                        for (int k = 0; k < 3; k++) {
                            org.eclipse.swt.widgets.TreeItem subsubItem = new org.eclipse.swt.widgets.TreeItem( subItem, SWT.NONE );
                            subsubItem.setText( 0, prefix + " name " + i + " " + j + " " + k );
                            subsubItem.setText( 1, prefix + " value " + i + " " + j + " " + k );
                            subsubItem.setText( 2, prefix + " description " + i + " " + j + " " + k );
                            subsubItem.setImage( itemImage );
                        }
                    }
                }
                column0.pack();
                column1.pack();
                column2.pack();
                return tree;
            }

        case CANVAS :
            {
                org.eclipse.swt.widgets.Canvas canvas = new org.eclipse.swt.widgets.Canvas( parent, SWT.BORDER );
                canvas.setData( "STRINGS", new java.lang.String[]{ prefix + " Canvas widget" } );
                canvas.addPaintListener( new org.eclipse.swt.events.PaintListener(){
                    public void paintControl( org.eclipse.swt.events.PaintEvent e )
                    {
                        org.eclipse.swt.widgets.Canvas c = (org.eclipse.swt.widgets.Canvas) e.widget;
                        org.eclipse.swt.graphics.Image image = (org.eclipse.swt.graphics.Image) c.getData( "IMAGE" );
                        if (image != null) {
                            e.gc.drawImage( image, 5, 5 );
                        } else {
                            java.lang.String[] strings = (java.lang.String[]) c.getData( "STRINGS" );
                            if (strings != null) {
                                org.eclipse.swt.graphics.FontMetrics metrics = e.gc.getFontMetrics();
                                int height = metrics.getHeight();
                                int y = 5;
                                for (int i = 0; i < strings.length; i++) {
                                    e.gc.drawString( strings[i], 5, y );
                                    y += height + 5;
                                }
                            }
                        }
                    }
                } );
                return canvas;
            }

        case LABEL :
            {
                org.eclipse.swt.widgets.Label label = new org.eclipse.swt.widgets.Label( parent, SWT.BORDER );
                label.setText( prefix + " Label" );
                return label;
            }

        case LIST :
            {
                org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List( parent, SWT.BORDER );
                list.setItems( new java.lang.String[]{ prefix + " Item a", prefix + " Item b", prefix + " Item c", prefix + " Item d" } );
                return list;
            }

        case COMBO :
            {
                org.eclipse.swt.widgets.Combo combo = new org.eclipse.swt.widgets.Combo( parent, SWT.BORDER );
                combo.setItems( new java.lang.String[]{ "Item a", "Item b", "Item c", "Item d" } );
                return combo;
            }

        default  :
            throw new org.eclipse.swt.SWTError( SWT.ERROR_NOT_IMPLEMENTED );

        }
    }

    public void open( org.eclipse.swt.widgets.Display display )
    {
        org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell( display );
        shell.setText( "Drag and Drop Example" );
        shell.setLayout( new org.eclipse.swt.layout.FillLayout() );
        itemImage = new org.eclipse.swt.graphics.Image( display, (org.eclipse.swt.examples.dnd.DNDExample.class).getResourceAsStream( "openFolder.gif" ) );
        org.eclipse.swt.custom.ScrolledComposite sc = new org.eclipse.swt.custom.ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
        org.eclipse.swt.widgets.Composite parent = new org.eclipse.swt.widgets.Composite( sc, SWT.NONE );
        sc.setContent( parent );
        parent.setLayout( new org.eclipse.swt.layout.FormLayout() );
        org.eclipse.swt.widgets.Label dragLabel = new org.eclipse.swt.widgets.Label( parent, SWT.LEFT );
        dragLabel.setText( "Drag Source:" );
        org.eclipse.swt.widgets.Group dragWidgetGroup = new org.eclipse.swt.widgets.Group( parent, SWT.NONE );
        dragWidgetGroup.setText( "Widget" );
        createDragWidget( dragWidgetGroup );
        org.eclipse.swt.widgets.Composite cLeft = new org.eclipse.swt.widgets.Composite( parent, SWT.NONE );
        cLeft.setLayout( new org.eclipse.swt.layout.GridLayout( 2, false ) );
        org.eclipse.swt.widgets.Group dragOperationsGroup = new org.eclipse.swt.widgets.Group( cLeft, SWT.NONE );
        dragOperationsGroup.setLayoutData( new org.eclipse.swt.layout.GridData( SWT.LEFT, SWT.FILL, false, false, 1, 1 ) );
        dragOperationsGroup.setText( "Allowed Operation(s):" );
        createDragOperations( dragOperationsGroup );
        org.eclipse.swt.widgets.Group dragTypesGroup = new org.eclipse.swt.widgets.Group( cLeft, SWT.NONE );
        dragTypesGroup.setLayoutData( new org.eclipse.swt.layout.GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
        dragTypesGroup.setText( "Transfer Type(s):" );
        createDragTypes( dragTypesGroup );
        dragConsole = new org.eclipse.swt.widgets.Text( cLeft, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI );
        dragConsole.setLayoutData( new org.eclipse.swt.layout.GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
        org.eclipse.swt.widgets.Menu menu = new org.eclipse.swt.widgets.Menu( shell, SWT.POP_UP );
        org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem( menu, SWT.PUSH );
        item.setText( "Clear" );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                dragConsole.setText( "" );
            }
        } );
        item = new org.eclipse.swt.widgets.MenuItem( menu, SWT.CHECK );
        item.setText( "Show Event detail" );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.MenuItem item = (org.eclipse.swt.widgets.MenuItem) e.widget;
                dragEventDetail = item.getSelection();
            }
        } );
        dragConsole.setMenu( menu );
        org.eclipse.swt.widgets.Label dropLabel = new org.eclipse.swt.widgets.Label( parent, SWT.LEFT );
        dropLabel.setText( "Drop Target:" );
        org.eclipse.swt.widgets.Group dropWidgetGroup = new org.eclipse.swt.widgets.Group( parent, SWT.NONE );
        dropWidgetGroup.setText( "Widget" );
        createDropWidget( dropWidgetGroup );
        org.eclipse.swt.widgets.Composite cRight = new org.eclipse.swt.widgets.Composite( parent, SWT.NONE );
        cRight.setLayout( new org.eclipse.swt.layout.GridLayout( 2, false ) );
        org.eclipse.swt.widgets.Group dropOperationsGroup = new org.eclipse.swt.widgets.Group( cRight, SWT.NONE );
        dropOperationsGroup.setLayoutData( new org.eclipse.swt.layout.GridData( SWT.LEFT, SWT.FILL, false, false, 1, 2 ) );
        dropOperationsGroup.setText( "Allowed Operation(s):" );
        createDropOperations( dropOperationsGroup );
        org.eclipse.swt.widgets.Group dropTypesGroup = new org.eclipse.swt.widgets.Group( cRight, SWT.NONE );
        dropTypesGroup.setText( "Transfer Type(s):" );
        createDropTypes( dropTypesGroup );
        org.eclipse.swt.widgets.Group feedbackTypesGroup = new org.eclipse.swt.widgets.Group( cRight, SWT.NONE );
        feedbackTypesGroup.setText( "Feedback Type(s):" );
        createFeedbackTypes( feedbackTypesGroup );
        dropConsole = new org.eclipse.swt.widgets.Text( cRight, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI );
        dropConsole.setLayoutData( new org.eclipse.swt.layout.GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
        menu = new org.eclipse.swt.widgets.Menu( shell, SWT.POP_UP );
        item = new org.eclipse.swt.widgets.MenuItem( menu, SWT.PUSH );
        item.setText( "Clear" );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                dropConsole.setText( "" );
            }
        } );
        item = new org.eclipse.swt.widgets.MenuItem( menu, SWT.CHECK );
        item.setText( "Show Event detail" );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.MenuItem item = (org.eclipse.swt.widgets.MenuItem) e.widget;
                dropEventDetail = item.getSelection();
            }
        } );
        dropConsole.setMenu( menu );
        if (dragEnabled) {
            createDragSource();
        }
        if (dropEnabled) {
            createDropTarget();
        }
        int height = 200;
        org.eclipse.swt.layout.FormData data = new org.eclipse.swt.layout.FormData();
        data.top = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        dragLabel.setLayoutData( data );
        data = new org.eclipse.swt.layout.FormData();
        data.top = new org.eclipse.swt.layout.FormAttachment( dragLabel, 10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        data.right = new org.eclipse.swt.layout.FormAttachment( 50, -10 );
        data.height = height;
        dragWidgetGroup.setLayoutData( data );
        data = new org.eclipse.swt.layout.FormData();
        data.top = new org.eclipse.swt.layout.FormAttachment( dragWidgetGroup, 10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        data.right = new org.eclipse.swt.layout.FormAttachment( 50, -10 );
        data.bottom = new org.eclipse.swt.layout.FormAttachment( 100, -10 );
        cLeft.setLayoutData( data );
        data = new org.eclipse.swt.layout.FormData();
        data.top = new org.eclipse.swt.layout.FormAttachment( 0, 10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( cLeft, 10 );
        dropLabel.setLayoutData( data );
        data = new org.eclipse.swt.layout.FormData();
        data.top = new org.eclipse.swt.layout.FormAttachment( dropLabel, 10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( cLeft, 10 );
        data.right = new org.eclipse.swt.layout.FormAttachment( 100, -10 );
        data.height = height;
        dropWidgetGroup.setLayoutData( data );
        data = new org.eclipse.swt.layout.FormData();
        data.top = new org.eclipse.swt.layout.FormAttachment( dropWidgetGroup, 10 );
        data.left = new org.eclipse.swt.layout.FormAttachment( cLeft, 10 );
        data.right = new org.eclipse.swt.layout.FormAttachment( 100, -10 );
        data.bottom = new org.eclipse.swt.layout.FormAttachment( 100, -10 );
        cRight.setLayoutData( data );
        sc.setMinSize( parent.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
        sc.setExpandHorizontal( true );
        sc.setExpandVertical( true );
        org.eclipse.swt.graphics.Point size = shell.computeSize( SWT.DEFAULT, SWT.DEFAULT );
        org.eclipse.swt.graphics.Rectangle monitorArea = shell.getMonitor().getClientArea();
        shell.setSize( Math.min( size.x, monitorArea.width - 20 ), Math.min( size.y, monitorArea.height - 20 ) );
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        itemImage.dispose();
    }

    private void printEvent( org.eclipse.swt.dnd.DragSourceEvent e )
    {
        if (!dragEventDetail) {
            return;
        }
        dragConsole.append( e.toString() + "\n" );
    }

    private void printEvent( org.eclipse.swt.dnd.DropTargetEvent e )
    {
        if (!dropEventDetail) {
            return;
        }
        dropConsole.append( e.toString() + "\n" );
    }

    private void removeDragTransfer( org.eclipse.swt.dnd.Transfer transfer )
    {
        if (dragTypes.length == 1) {
            dragTypes = new org.eclipse.swt.dnd.Transfer[0];
        } else {
            int index = -1;
            for (int i = 0; i < dragTypes.length; i++) {
                if (dragTypes[i] == transfer) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                return;
            }
            org.eclipse.swt.dnd.Transfer[] newTypes = new org.eclipse.swt.dnd.Transfer[dragTypes.length - 1];
            System.arraycopy( dragTypes, 0, newTypes, 0, index );
            System.arraycopy( dragTypes, index + 1, newTypes, index, dragTypes.length - index - 1 );
            dragTypes = newTypes;
        }
        if (dragSource != null) {
            dragSource.setTransfer( dragTypes );
        }
    }

    private void removeDropTransfer( org.eclipse.swt.dnd.Transfer transfer )
    {
        if (dropTypes.length == 1) {
            dropTypes = new org.eclipse.swt.dnd.Transfer[0];
        } else {
            int index = -1;
            for (int i = 0; i < dropTypes.length; i++) {
                if (dropTypes[i] == transfer) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                return;
            }
            org.eclipse.swt.dnd.Transfer[] newTypes = new org.eclipse.swt.dnd.Transfer[dropTypes.length - 1];
            System.arraycopy( dropTypes, 0, newTypes, 0, index );
            System.arraycopy( dropTypes, index + 1, newTypes, index, dropTypes.length - index - 1 );
            dropTypes = newTypes;
        }
        if (dropTarget != null) {
            dropTarget.setTransfer( dropTypes );
        }
    }

}
