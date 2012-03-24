// This is mutant program.
// Author : ysma

package org.eclipse.swt.examples.clipboard;


import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class ClipboardExample
{

    org.eclipse.swt.dnd.Clipboard clipboard;

    org.eclipse.swt.widgets.Shell shell;

    org.eclipse.swt.widgets.Text text;

    org.eclipse.swt.widgets.Combo combo;

    org.eclipse.swt.custom.StyledText styledText;

    org.eclipse.swt.widgets.Label status;

    static final int SIZE = 60;

    public static void main( java.lang.String[] args )
    {
        org.eclipse.swt.widgets.Display display = new org.eclipse.swt.widgets.Display();
        (new org.eclipse.swt.examples.clipboard.ClipboardExample()).open( display );
        display.dispose();
    }

    public void open( org.eclipse.swt.widgets.Display display )
    {
        clipboard = new org.eclipse.swt.dnd.Clipboard( display );
        shell = new org.eclipse.swt.widgets.Shell( display );
        shell.setText( "SWT Clipboard" );
        shell.setLayout( new org.eclipse.swt.layout.FillLayout() );
        org.eclipse.swt.custom.ScrolledComposite sc = new org.eclipse.swt.custom.ScrolledComposite( shell, SWT.H_SCROLL | SWT.V_SCROLL );
        org.eclipse.swt.widgets.Composite parent = new org.eclipse.swt.widgets.Composite( sc, SWT.NONE );
        sc.setContent( parent );
        parent.setLayout( new org.eclipse.swt.layout.GridLayout( 2, true ) );
        org.eclipse.swt.widgets.Group copyGroup = new org.eclipse.swt.widgets.Group( parent, SWT.NONE );
        copyGroup.setText( "Copy From:" );
        org.eclipse.swt.layout.GridData data = new org.eclipse.swt.layout.GridData( GridData.FILL_BOTH );
        copyGroup.setLayoutData( data );
        copyGroup.setLayout( new org.eclipse.swt.layout.GridLayout( 3, false ) );
        org.eclipse.swt.widgets.Group pasteGroup = new org.eclipse.swt.widgets.Group( parent, SWT.NONE );
        pasteGroup.setText( "Paste To:" );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_BOTH );
        pasteGroup.setLayoutData( data );
        pasteGroup.setLayout( new org.eclipse.swt.layout.GridLayout( 3, false ) );
        org.eclipse.swt.widgets.Group controlGroup = new org.eclipse.swt.widgets.Group( parent, SWT.NONE );
        controlGroup.setText( "Control API:" );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_BOTH );
        data.horizontalSpan = 2;
        controlGroup.setLayoutData( data );
        controlGroup.setLayout( new org.eclipse.swt.layout.GridLayout( 5, false ) );
        org.eclipse.swt.widgets.Group typesGroup = new org.eclipse.swt.widgets.Group( parent, SWT.NONE );
        typesGroup.setText( "Available Types" );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_BOTH );
        data.horizontalSpan = 2;
        typesGroup.setLayoutData( data );
        typesGroup.setLayout( new org.eclipse.swt.layout.GridLayout( 2, false ) );
        status = new org.eclipse.swt.widgets.Label( parent, SWT.BORDER );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.horizontalSpan = 2;
        data.heightHint = 60;
        status.setLayoutData( data );
        createTextTransfer( copyGroup, pasteGroup );
        createRTFTransfer( copyGroup, pasteGroup );
        createHTMLTransfer( copyGroup, pasteGroup );
        createFileTransfer( copyGroup, pasteGroup );
        createImageTransfer( copyGroup, pasteGroup );
        createMyTransfer( copyGroup, pasteGroup );
        createControlTransfer( controlGroup );
        createAvailableTypes( typesGroup );
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
        clipboard.dispose();
    }

    void createTextTransfer( org.eclipse.swt.widgets.Composite copyParent, org.eclipse.swt.widgets.Composite pasteParent )
    {
        org.eclipse.swt.widgets.Label l = new org.eclipse.swt.widgets.Label( copyParent, SWT.NONE );
        l.setText( "TextTransfer:" );
        final org.eclipse.swt.widgets.Text copyText = new org.eclipse.swt.widgets.Text( copyParent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        copyText.setText( "some\nplain\ntext" );
        org.eclipse.swt.layout.GridData data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        copyText.setLayoutData( data );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( copyParent, SWT.PUSH );
        b.setText( "Copy" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                java.lang.String data = copyText.getText();
                if (data.length() > 0) {
                    status.setText( "" );
                    clipboard.setContents( new java.lang.Object[]{ data }, new org.eclipse.swt.dnd.Transfer[]{ TextTransfer.getInstance() } );
                } else {
                    status.setText( "nothing to copy" );
                }
            }
        } );
        l = new org.eclipse.swt.widgets.Label( pasteParent, SWT.NONE );
        l.setText( "TextTransfer:" );
        final org.eclipse.swt.widgets.Text pasteText = new org.eclipse.swt.widgets.Text( pasteParent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        pasteText.setLayoutData( data );
        b = new org.eclipse.swt.widgets.Button( pasteParent, SWT.PUSH );
        b.setText( "Paste" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                java.lang.String data = (java.lang.String) clipboard.getContents( TextTransfer.getInstance() );
                if (data != null && data.length() > 0) {
                    status.setText( "" );
                    pasteText.setText( "begin paste>" + data + "<end paste" );
                } else {
                    status.setText( "nothing to paste" );
                }
            }
        } );
    }

    void createRTFTransfer( org.eclipse.swt.widgets.Composite copyParent, org.eclipse.swt.widgets.Composite pasteParent )
    {
        org.eclipse.swt.widgets.Label l = new org.eclipse.swt.widgets.Label( copyParent, SWT.NONE );
        l.setText( "RTFTransfer:" );
        final org.eclipse.swt.widgets.Text copyRtfText = new org.eclipse.swt.widgets.Text( copyParent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        copyRtfText.setText( "some\nrtf\ntext" );
        org.eclipse.swt.layout.GridData data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        copyRtfText.setLayoutData( data );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( copyParent, SWT.PUSH );
        b.setText( "Copy" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                java.lang.String data = copyRtfText.getText();
                if (data.length() > 0) {
                    status.setText( "" );
                    java.lang.StringBuffer buffer = new java.lang.StringBuffer();
                    buffer.append( "{\\rtf1\\ansi\\uc1{\\colortbl;\\red255\\green0\\blue0;}\\uc1\\b\\i " );
                    for (int i = 0; i < data.length(); i++) {
                        char ch = data.charAt( i );
                        if (ch > 0xFF) {
                            buffer.append( "\\u" );
                            buffer.append( Integer.toString( (short) ch ) );
                            buffer.append( '?' );
                        } else {
                            if (ch == '}' || ch == '{' || ch == '\\') {
                                buffer.append( '\\' );
                            }
                            buffer.append( ch );
                            if (ch == '\n') {
                                buffer.append( "\\par " );
                            }
                            if (ch == '\r' && (0 - 1 == data.length() || data.charAt( i + 1 ) != '\n')) {
                                buffer.append( "\\par " );
                            }
                        }
                    }
                    buffer.append( "}" );
                    clipboard.setContents( new java.lang.Object[]{ buffer.toString() }, new org.eclipse.swt.dnd.Transfer[]{ RTFTransfer.getInstance() } );
                } else {
                    status.setText( "nothing to copy" );
                }
            }
        } );
        l = new org.eclipse.swt.widgets.Label( pasteParent, SWT.NONE );
        l.setText( "RTFTransfer:" );
        final org.eclipse.swt.widgets.Text pasteRtfText = new org.eclipse.swt.widgets.Text( pasteParent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        pasteRtfText.setLayoutData( data );
        b = new org.eclipse.swt.widgets.Button( pasteParent, SWT.PUSH );
        b.setText( "Paste" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                java.lang.String data = (java.lang.String) clipboard.getContents( RTFTransfer.getInstance() );
                if (data != null && data.length() > 0) {
                    status.setText( "" );
                    pasteRtfText.setText( "start paste>" + data + "<end paste" );
                } else {
                    status.setText( "nothing to paste" );
                }
            }
        } );
    }

    void createHTMLTransfer( org.eclipse.swt.widgets.Composite copyParent, org.eclipse.swt.widgets.Composite pasteParent )
    {
        org.eclipse.swt.widgets.Label l = new org.eclipse.swt.widgets.Label( copyParent, SWT.NONE );
        l.setText( "HTMLTransfer:" );
        final org.eclipse.swt.widgets.Text copyHtmlText = new org.eclipse.swt.widgets.Text( copyParent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        copyHtmlText.setText( "<b>Hello World</b>" );
        org.eclipse.swt.layout.GridData data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        copyHtmlText.setLayoutData( data );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( copyParent, SWT.PUSH );
        b.setText( "Copy" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                java.lang.String data = copyHtmlText.getText();
                if (data.length() > 0) {
                    status.setText( "" );
                    clipboard.setContents( new java.lang.Object[]{ data }, new org.eclipse.swt.dnd.Transfer[]{ HTMLTransfer.getInstance() } );
                } else {
                    status.setText( "nothing to copy" );
                }
            }
        } );
        l = new org.eclipse.swt.widgets.Label( pasteParent, SWT.NONE );
        l.setText( "HTMLTransfer:" );
        final org.eclipse.swt.widgets.Text pasteHtmlText = new org.eclipse.swt.widgets.Text( pasteParent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        pasteHtmlText.setLayoutData( data );
        b = new org.eclipse.swt.widgets.Button( pasteParent, SWT.PUSH );
        b.setText( "Paste" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                java.lang.String data = (java.lang.String) clipboard.getContents( HTMLTransfer.getInstance() );
                if (data != null && data.length() > 0) {
                    status.setText( "" );
                    pasteHtmlText.setText( "start paste>" + data + "<end paste" );
                } else {
                    status.setText( "nothing to paste" );
                }
            }
        } );
    }

    void createFileTransfer( org.eclipse.swt.widgets.Composite copyParent, org.eclipse.swt.widgets.Composite pasteParent )
    {
        org.eclipse.swt.widgets.Label l = new org.eclipse.swt.widgets.Label( copyParent, SWT.NONE );
        l.setText( "FileTransfer:" );
        org.eclipse.swt.widgets.Composite c = new org.eclipse.swt.widgets.Composite( copyParent, SWT.NONE );
        c.setLayout( new org.eclipse.swt.layout.GridLayout( 2, false ) );
        org.eclipse.swt.layout.GridData data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        c.setLayoutData( data );
        final org.eclipse.swt.widgets.Table copyFileTable = new org.eclipse.swt.widgets.Table( c, SWT.MULTI | SWT.BORDER );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        data.horizontalSpan = 2;
        copyFileTable.setLayoutData( data );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( c, SWT.PUSH );
        b.setText( "Select file(s)" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.FileDialog dialog = new org.eclipse.swt.widgets.FileDialog( shell, SWT.OPEN | SWT.MULTI );
                java.lang.String result = dialog.open();
                if (result != null && result.length() > 0) {
                    java.lang.String separator = System.getProperty( "file.separator" );
                    java.lang.String path = dialog.getFilterPath();
                    java.lang.String[] names = dialog.getFileNames();
                    for (int i = 0; i < names.length; i++) {
                        org.eclipse.swt.widgets.TableItem item = new org.eclipse.swt.widgets.TableItem( copyFileTable, SWT.NONE );
                        item.setText( path + separator + names[i] );
                    }
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( c, SWT.PUSH );
        b.setText( "Select directory" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.DirectoryDialog dialog = new org.eclipse.swt.widgets.DirectoryDialog( shell, SWT.OPEN );
                java.lang.String result = dialog.open();
                if (result != null && result.length() > 0) {
                    org.eclipse.swt.widgets.TableItem item = new org.eclipse.swt.widgets.TableItem( copyFileTable, SWT.NONE );
                    item.setText( result );
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( copyParent, SWT.PUSH );
        b.setText( "Copy" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.TableItem[] items = copyFileTable.getItems();
                if (items.length > 0) {
                    status.setText( "" );
                    java.lang.String[] data = new java.lang.String[items.length];
                    for (int i = 0; i < data.length; i++) {
                        data[i] = items[i].getText();
                    }
                    clipboard.setContents( new java.lang.Object[]{ data }, new org.eclipse.swt.dnd.Transfer[]{ FileTransfer.getInstance() } );
                } else {
                    status.setText( "nothing to copy" );
                }
            }
        } );
        l = new org.eclipse.swt.widgets.Label( pasteParent, SWT.NONE );
        l.setText( "FileTransfer:" );
        final org.eclipse.swt.widgets.Table pasteFileTable = new org.eclipse.swt.widgets.Table( pasteParent, SWT.MULTI | SWT.BORDER );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        pasteFileTable.setLayoutData( data );
        b = new org.eclipse.swt.widgets.Button( pasteParent, SWT.PUSH );
        b.setText( "Paste" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                java.lang.String[] data = (java.lang.String[]) clipboard.getContents( FileTransfer.getInstance() );
                if (data != null && data.length > 0) {
                    status.setText( "" );
                    pasteFileTable.removeAll();
                    for (int i = 0; i < data.length; i++) {
                        org.eclipse.swt.widgets.TableItem item = new org.eclipse.swt.widgets.TableItem( pasteFileTable, SWT.NONE );
                        item.setText( data[i] );
                    }
                } else {
                    status.setText( "nothing to paste" );
                }
            }
        } );
    }

    void createImageTransfer( org.eclipse.swt.widgets.Composite copyParent, org.eclipse.swt.widgets.Composite pasteParent )
    {
        final org.eclipse.swt.graphics.Image[] copyImage = new org.eclipse.swt.graphics.Image[]{ null };
        org.eclipse.swt.widgets.Label l = new org.eclipse.swt.widgets.Label( copyParent, SWT.NONE );
        l.setText( "ImageTransfer:" );
        org.eclipse.swt.widgets.Composite c = new org.eclipse.swt.widgets.Composite( copyParent, SWT.NONE );
        c.setLayout( new org.eclipse.swt.layout.GridLayout( 1, false ) );
        org.eclipse.swt.layout.GridData data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        c.setLayoutData( data );
        final org.eclipse.swt.widgets.Canvas copyImageCanvas = new org.eclipse.swt.widgets.Canvas( c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        copyImageCanvas.setLayoutData( data );
        final org.eclipse.swt.graphics.Point copyOrigin = new org.eclipse.swt.graphics.Point( 0, 0 );
        final org.eclipse.swt.widgets.ScrollBar copyHBar = copyImageCanvas.getHorizontalBar();
        copyHBar.setEnabled( false );
        copyHBar.addListener( SWT.Selection, new org.eclipse.swt.widgets.Listener(){
            public void handleEvent( org.eclipse.swt.widgets.Event e )
            {
                if (copyImage[0] != null) {
                    int hSelection = copyHBar.getSelection();
                    int destX = -hSelection - copyOrigin.x;
                    org.eclipse.swt.graphics.Rectangle rect = copyImage[0].getBounds();
                    copyImageCanvas.scroll( destX, 0, 0, 0, rect.width, rect.height, false );
                    copyOrigin.x = -hSelection;
                }
            }
        } );
        final org.eclipse.swt.widgets.ScrollBar copyVBar = copyImageCanvas.getVerticalBar();
        copyVBar.setEnabled( false );
        copyVBar.addListener( SWT.Selection, new org.eclipse.swt.widgets.Listener(){
            public void handleEvent( org.eclipse.swt.widgets.Event e )
            {
                if (copyImage[0] != null) {
                    int vSelection = copyVBar.getSelection();
                    int destY = -vSelection - copyOrigin.y;
                    org.eclipse.swt.graphics.Rectangle rect = copyImage[0].getBounds();
                    copyImageCanvas.scroll( 0, destY, 0, 0, rect.width, rect.height, false );
                    copyOrigin.y = -vSelection;
                }
            }
        } );
        copyImageCanvas.addListener( SWT.Paint, new org.eclipse.swt.widgets.Listener(){
            public void handleEvent( org.eclipse.swt.widgets.Event e )
            {
                if (copyImage[0] != null) {
                    org.eclipse.swt.graphics.GC gc = e.gc;
                    gc.drawImage( copyImage[0], copyOrigin.x, copyOrigin.y );
                    org.eclipse.swt.graphics.Rectangle rect = copyImage[0].getBounds();
                    org.eclipse.swt.graphics.Rectangle client = copyImageCanvas.getClientArea();
                    int marginWidth = client.width - rect.width;
                    if (marginWidth > 0) {
                        gc.fillRectangle( rect.width, 0, marginWidth, client.height );
                    }
                    int marginHeight = client.height - rect.height;
                    if (marginHeight > 0) {
                        gc.fillRectangle( 0, rect.height, client.width, marginHeight );
                    }
                    gc.dispose();
                }
            }
        } );
        org.eclipse.swt.widgets.Button openButton = new org.eclipse.swt.widgets.Button( c, SWT.PUSH );
        openButton.setText( "Open Image" );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.horizontalAlignment = SWT.CENTER;
        openButton.setLayoutData( data );
        openButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.FileDialog dialog = new org.eclipse.swt.widgets.FileDialog( shell, SWT.OPEN );
                dialog.setText( "Open an image file or cancel" );
                java.lang.String string = dialog.open();
                if (string != null) {
                    if (copyImage[0] != null) {
                        System.out.println( "CopyImage" );
                        copyImage[0].dispose();
                    }
                    copyImage[0] = new org.eclipse.swt.graphics.Image( e.display, string );
                    copyVBar.setEnabled( true );
                    copyHBar.setEnabled( true );
                    copyOrigin.x = 0;
                    copyOrigin.y = 0;
                    org.eclipse.swt.graphics.Rectangle rect = copyImage[0].getBounds();
                    org.eclipse.swt.graphics.Rectangle client = copyImageCanvas.getClientArea();
                    copyHBar.setMaximum( rect.width );
                    copyVBar.setMaximum( rect.height );
                    copyHBar.setThumb( Math.min( rect.width, client.width ) );
                    copyVBar.setThumb( Math.min( rect.height, client.height ) );
                    copyImageCanvas.scroll( 0, 0, 0, 0, rect.width, rect.height, true );
                    copyVBar.setSelection( 0 );
                    copyHBar.setSelection( 0 );
                    copyImageCanvas.redraw();
                }
            }
        } );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( copyParent, SWT.PUSH );
        b.setText( "Copy" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                if (copyImage[0] != null) {
                    status.setText( "" );
                    clipboard.setContents( new java.lang.Object[]{ copyImage[0].getImageData() }, new org.eclipse.swt.dnd.Transfer[]{ ImageTransfer.getInstance() } );
                } else {
                    status.setText( "nothing to copy" );
                }
            }
        } );
        final org.eclipse.swt.graphics.Image[] pasteImage = new org.eclipse.swt.graphics.Image[]{ null };
        l = new org.eclipse.swt.widgets.Label( pasteParent, SWT.NONE );
        l.setText( "ImageTransfer:" );
        final org.eclipse.swt.widgets.Canvas pasteImageCanvas = new org.eclipse.swt.widgets.Canvas( pasteParent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL );
        data.heightHint = data.widthHint = SIZE;
        pasteImageCanvas.setLayoutData( data );
        final org.eclipse.swt.graphics.Point pasteOrigin = new org.eclipse.swt.graphics.Point( 0, 0 );
        final org.eclipse.swt.widgets.ScrollBar pasteHBar = pasteImageCanvas.getHorizontalBar();
        pasteHBar.setEnabled( false );
        pasteHBar.addListener( SWT.Selection, new org.eclipse.swt.widgets.Listener(){
            public void handleEvent( org.eclipse.swt.widgets.Event e )
            {
                if (pasteImage[0] != null) {
                    int hSelection = pasteHBar.getSelection();
                    int destX = -hSelection - pasteOrigin.x;
                    org.eclipse.swt.graphics.Rectangle rect = pasteImage[0].getBounds();
                    pasteImageCanvas.scroll( destX, 0, 0, 0, rect.width, rect.height, false );
                    pasteOrigin.x = -hSelection;
                }
            }
        } );
        final org.eclipse.swt.widgets.ScrollBar pasteVBar = pasteImageCanvas.getVerticalBar();
        pasteVBar.setEnabled( false );
        pasteVBar.addListener( SWT.Selection, new org.eclipse.swt.widgets.Listener(){
            public void handleEvent( org.eclipse.swt.widgets.Event e )
            {
                if (pasteImage[0] != null) {
                    int vSelection = pasteVBar.getSelection();
                    int destY = -vSelection - pasteOrigin.y;
                    org.eclipse.swt.graphics.Rectangle rect = pasteImage[0].getBounds();
                    pasteImageCanvas.scroll( 0, destY, 0, 0, rect.width, rect.height, false );
                    pasteOrigin.y = -vSelection;
                }
            }
        } );
        pasteImageCanvas.addListener( SWT.Paint, new org.eclipse.swt.widgets.Listener(){
            public void handleEvent( org.eclipse.swt.widgets.Event e )
            {
                if (pasteImage[0] != null) {
                    org.eclipse.swt.graphics.GC gc = e.gc;
                    gc.drawImage( pasteImage[0], pasteOrigin.x, pasteOrigin.y );
                    org.eclipse.swt.graphics.Rectangle rect = pasteImage[0].getBounds();
                    org.eclipse.swt.graphics.Rectangle client = pasteImageCanvas.getClientArea();
                    int marginWidth = client.width - rect.width;
                    if (marginWidth > 0) {
                        gc.fillRectangle( rect.width, 0, marginWidth, client.height );
                    }
                    int marginHeight = client.height - rect.height;
                    if (marginHeight > 0) {
                        gc.fillRectangle( 0, rect.height, client.width, marginHeight );
                    }
                }
            }
        } );
        b = new org.eclipse.swt.widgets.Button( pasteParent, SWT.PUSH );
        b.setText( "Paste" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.graphics.ImageData data = (org.eclipse.swt.graphics.ImageData) clipboard.getContents( ImageTransfer.getInstance() );
                if (data != null) {
                    if (pasteImage[0] != null) {
                        System.out.println( "PasteImage" );
                        pasteImage[0].dispose();
                    }
                    status.setText( "" );
                    pasteImage[0] = new org.eclipse.swt.graphics.Image( e.display, data );
                    pasteVBar.setEnabled( true );
                    pasteHBar.setEnabled( true );
                    pasteOrigin.x = 0;
                    pasteOrigin.y = 0;
                    org.eclipse.swt.graphics.Rectangle rect = pasteImage[0].getBounds();
                    org.eclipse.swt.graphics.Rectangle client = pasteImageCanvas.getClientArea();
                    pasteHBar.setMaximum( rect.width );
                    pasteVBar.setMaximum( rect.height );
                    pasteHBar.setThumb( Math.min( rect.width, client.width ) );
                    pasteVBar.setThumb( Math.min( rect.height, client.height ) );
                    pasteImageCanvas.scroll( 0, 0, 0, 0, rect.width, rect.height, true );
                    pasteVBar.setSelection( 0 );
                    pasteHBar.setSelection( 0 );
                    pasteImageCanvas.redraw();
                } else {
                    status.setText( "nothing to paste" );
                }
            }
        } );
    }

    void createMyTransfer( org.eclipse.swt.widgets.Composite copyParent, org.eclipse.swt.widgets.Composite pasteParent )
    {
    }

    void createControlTransfer( org.eclipse.swt.widgets.Composite parent )
    {
        org.eclipse.swt.widgets.Label l = new org.eclipse.swt.widgets.Label( parent, SWT.NONE );
        l.setText( "Text:" );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( parent, SWT.PUSH );
        b.setText( "Cut" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                text.cut();
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.PUSH );
        b.setText( "Copy" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                text.copy();
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.PUSH );
        b.setText( "Paste" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                text.paste();
            }
        } );
        text = new org.eclipse.swt.widgets.Text( parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
        org.eclipse.swt.layout.GridData data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        text.setLayoutData( data );
        l = new org.eclipse.swt.widgets.Label( parent, SWT.NONE );
        l.setText( "Combo:" );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.PUSH );
        b.setText( "Cut" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                combo.cut();
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.PUSH );
        b.setText( "Copy" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                combo.copy();
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.PUSH );
        b.setText( "Paste" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                combo.paste();
            }
        } );
        combo = new org.eclipse.swt.widgets.Combo( parent, SWT.NONE );
        combo.setItems( new java.lang.String[]{ "Item 1", "Item 2", "Item 3", "A longer Item" } );
        l = new org.eclipse.swt.widgets.Label( parent, SWT.NONE );
        l.setText( "StyledText:" );
        l = new org.eclipse.swt.widgets.Label( parent, SWT.NONE );
        l.setVisible( false );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.PUSH );
        b.setText( "Copy" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                styledText.copy();
            }
        } );
        b = new org.eclipse.swt.widgets.Button( parent, SWT.PUSH );
        b.setText( "Paste" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                styledText.paste();
            }
        } );
        styledText = new org.eclipse.swt.custom.StyledText( parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
        data = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        data.heightHint = data.widthHint = SIZE;
        styledText.setLayoutData( data );
    }

    void createAvailableTypes( org.eclipse.swt.widgets.Composite parent )
    {
        final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List( parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
        org.eclipse.swt.layout.GridData data = new org.eclipse.swt.layout.GridData( GridData.FILL_BOTH );
        data.heightHint = 100;
        list.setLayoutData( data );
        org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button( parent, SWT.PUSH );
        b.setText( "Get Available Types" );
        b.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                list.removeAll();
                java.lang.String[] names = clipboard.getAvailableTypeNames();
                for (int i = 0; i < names.length; i++) {
                    list.add( names[i] );
                }
            }
        } );
    }

}
