// This is mutant program.
// Author : ysma

package org.eclipse.swt.examples.hoverhelp;


import java.io.*;
import java.text.*;
import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class HoverHelp
{

    private static java.util.ResourceBundle resourceBundle = ResourceBundle.getBundle( "examples_hoverhelp" );

    static final int hhiInformation = 0;

    static final int hhiWarning = 1;

    static final java.lang.String[] imageLocations = { "information.gif", "warning.gif" };

    org.eclipse.swt.graphics.Image[] images;

    public static void main( java.lang.String[] args )
    {
        org.eclipse.swt.widgets.Display display = new org.eclipse.swt.widgets.Display();
        org.eclipse.swt.widgets.Shell shell = (new org.eclipse.swt.examples.hoverhelp.HoverHelp()).open( display );
        while (shell != null && !shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    public org.eclipse.swt.widgets.Shell open( org.eclipse.swt.widgets.Display display )
    {
        java.lang.Class clazz = org.eclipse.swt.examples.hoverhelp.HoverHelp.class;
        try {
            if (images == null) {
                images = new org.eclipse.swt.graphics.Image[imageLocations.length];
                for (int i = 0; i < imageLocations.length; ++i) {
                    java.io.InputStream stream = clazz.getResourceAsStream( imageLocations[i] );
                    org.eclipse.swt.graphics.ImageData source = new org.eclipse.swt.graphics.ImageData( stream );
                    org.eclipse.swt.graphics.ImageData mask = source.getTransparencyMask();
                    images[i] = new org.eclipse.swt.graphics.Image( display, source, mask );
                    try {
                        stream.close();
                    } catch ( java.io.IOException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } catch ( java.lang.Exception ex ) {
            System.err.println( getResourceString( "error.CouldNotLoadResources", new java.lang.Object[]{ ex.getMessage() } ) );
            return null;
        }
        org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell();
        createPartControl( shell );
        shell.addDisposeListener( new org.eclipse.swt.events.DisposeListener(){
            public void widgetDisposed( org.eclipse.swt.events.DisposeEvent e )
            {
                if (images != null) {
                    for (int i = 0; i < images.length; i++) {
                        final org.eclipse.swt.graphics.Image image = images[i];
                        if (image != null) {
                            image.dispose();
                        }
                    }
                    images = null;
                }
            }
        } );
        shell.pack();
        shell.open();
        return shell;
    }

    public java.lang.String getResourceString( java.lang.String key )
    {
        try {
            return resourceBundle.getString( key );
        } catch ( java.util.MissingResourceException e ) {
            return key;
        } catch ( java.lang.NullPointerException e ) {
            return "!" + key + "!";
        }
    }

    public java.lang.String getResourceString( java.lang.String key, java.lang.Object[] args )
    {
        try {
            return MessageFormat.format( getResourceString( key ), args );
        } catch ( java.util.MissingResourceException e ) {
            return key;
        } catch ( java.lang.NullPointerException e ) {
            return "!" + key + "!";
        }
    }

    public void createPartControl( org.eclipse.swt.widgets.Composite frame )
    {
        final org.eclipse.swt.examples.hoverhelp.HoverHelp.ToolTipHandler tooltip = new org.eclipse.swt.examples.hoverhelp.HoverHelp.ToolTipHandler( frame.getShell() );
        org.eclipse.swt.layout.GridLayout layout = new org.eclipse.swt.layout.GridLayout();
        layout.numColumns = 3;
        frame.setLayout( layout );
        java.lang.String platform = SWT.getPlatform();
        java.lang.String helpKey = "F1";
        if (platform.equals( "gtk" )) {
            helpKey = "Ctrl+F1";
        }
        if (platform.equals( "carbon" ) || platform.equals( "cocoa" )) {
            helpKey = "Help";
        }
        org.eclipse.swt.widgets.ToolBar bar = new org.eclipse.swt.widgets.ToolBar( frame, SWT.BORDER );
        for (int i = 0; i < 5; i++) {
            org.eclipse.swt.widgets.ToolItem item = new org.eclipse.swt.widgets.ToolItem( bar, SWT.PUSH );
            item.setText( getResourceString( "ToolItem.text", new java.lang.Object[]{ new java.lang.Integer( i ) } ) );
            item.setData( "TIP_TEXT", getResourceString( "ToolItem.tooltip", new java.lang.Object[]{ item.getText(), helpKey } ) );
            item.setData( "TIP_HELPTEXTHANDLER", new org.eclipse.swt.examples.hoverhelp.HoverHelp.ToolTipHelpTextHandler(){
                public java.lang.String getHelpText( org.eclipse.swt.widgets.Widget widget )
                {
                    org.eclipse.swt.widgets.Item item = (org.eclipse.swt.widgets.Item) widget;
                    return getResourceString( "ToolItem.help", new java.lang.Object[]{ item.getText() } );
                }
            } );
        }
        org.eclipse.swt.layout.GridData gridData = new org.eclipse.swt.layout.GridData();
        gridData.horizontalSpan = 3;
        bar.setLayoutData( gridData );
        tooltip.activateHoverHelp( bar );
        org.eclipse.swt.widgets.Table table = new org.eclipse.swt.widgets.Table( frame, SWT.BORDER );
        for (int i = 0; i < 4; i++) {
            org.eclipse.swt.widgets.TableItem item = new org.eclipse.swt.widgets.TableItem( table, SWT.PUSH );
            item.setText( getResourceString( "Item", new java.lang.Object[]{ new java.lang.Integer( i ) } ) );
            item.setData( "TIP_IMAGE", images[hhiInformation] );
            item.setText( getResourceString( "TableItem.text", new java.lang.Object[]{ new java.lang.Integer( i ) } ) );
            item.setData( "TIP_TEXT", getResourceString( "TableItem.tooltip", new java.lang.Object[]{ item.getText(), helpKey } ) );
            item.setData( "TIP_HELPTEXTHANDLER", new org.eclipse.swt.examples.hoverhelp.HoverHelp.ToolTipHelpTextHandler(){
                public java.lang.String getHelpText( org.eclipse.swt.widgets.Widget widget )
                {
                    org.eclipse.swt.widgets.Item item = (org.eclipse.swt.widgets.Item) widget;
                    return getResourceString( "TableItem.help", new java.lang.Object[]{ item.getText() } );
                }
            } );
        }
        table.setLayoutData( new org.eclipse.swt.layout.GridData( GridData.VERTICAL_ALIGN_FILL ) );
        tooltip.activateHoverHelp( table );
        org.eclipse.swt.widgets.Tree tree = new org.eclipse.swt.widgets.Tree( frame, SWT.BORDER );
        for (int i = 0; i < 4; i++) {
            org.eclipse.swt.widgets.TreeItem item = new org.eclipse.swt.widgets.TreeItem( tree, SWT.PUSH );
            item.setText( getResourceString( "Item", new java.lang.Object[]{ new java.lang.Integer( i ) } ) );
            item.setData( "TIP_IMAGE", images[hhiWarning] );
            item.setText( getResourceString( "TreeItem.text", new java.lang.Object[]{ new java.lang.Integer( i ) } ) );
            item.setData( "TIP_TEXT", getResourceString( "TreeItem.tooltip", new java.lang.Object[]{ item.getText(), helpKey } ) );
            item.setData( "TIP_HELPTEXTHANDLER", new org.eclipse.swt.examples.hoverhelp.HoverHelp.ToolTipHelpTextHandler(){
                public java.lang.String getHelpText( org.eclipse.swt.widgets.Widget widget )
                {
                    org.eclipse.swt.widgets.Item item = (org.eclipse.swt.widgets.Item) widget;
                    return getResourceString( "TreeItem.help", new java.lang.Object[]{ item.getText() } );
                }
            } );
        }
        tree.setLayoutData( new org.eclipse.swt.layout.GridData( GridData.VERTICAL_ALIGN_FILL ) );
        tooltip.activateHoverHelp( tree );
        org.eclipse.swt.widgets.Button button = new org.eclipse.swt.widgets.Button( frame, SWT.PUSH );
        button.setText( getResourceString( "Hello.text" ) );
        button.setData( "TIP_TEXT", getResourceString( "Hello.tooltip" ) );
        tooltip.activateHoverHelp( button );
    }

    protected static class ToolTipHandler
    {

        private org.eclipse.swt.widgets.Shell parentShell;

        private org.eclipse.swt.widgets.Shell tipShell;

        private org.eclipse.swt.widgets.Label tipLabelImage;

        private org.eclipse.swt.widgets.Label tipLabelText;

        private org.eclipse.swt.widgets.Widget tipWidget;

        private org.eclipse.swt.graphics.Point tipPosition;

        public ToolTipHandler( org.eclipse.swt.widgets.Shell parent )
        {
            final org.eclipse.swt.widgets.Display display = parent.getDisplay();
            this.parentShell = parent;
            tipShell = new org.eclipse.swt.widgets.Shell( parent, 0 );
            org.eclipse.swt.layout.GridLayout gridLayout = new org.eclipse.swt.layout.GridLayout();
            gridLayout.numColumns = 2;
            gridLayout.marginWidth = 2;
            gridLayout.marginHeight = 2;
            tipShell.setLayout( gridLayout );
            tipShell.setBackground( display.getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
            tipLabelImage = new org.eclipse.swt.widgets.Label( tipShell, SWT.NONE );
            tipLabelImage.setForeground( display.getSystemColor( SWT.COLOR_INFO_FOREGROUND ) );
            tipLabelImage.setBackground( display.getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
            tipLabelImage.setLayoutData( new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER ) );
            tipLabelText = new org.eclipse.swt.widgets.Label( tipShell, SWT.NONE );
            tipLabelText.setForeground( display.getSystemColor( SWT.COLOR_INFO_FOREGROUND ) );
            tipLabelText.setBackground( display.getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
            tipLabelText.setLayoutData( new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER ) );
        }

        public void activateHoverHelp( final org.eclipse.swt.widgets.Control control )
        {
            control.addMouseListener( new org.eclipse.swt.events.MouseAdapter(){
                public void mouseDown( org.eclipse.swt.events.MouseEvent e )
                {
                    if (tipShell.isVisible()) {
                        tipShell.setVisible( false );
                    }
                }
            } );
            control.addMouseTrackListener( new org.eclipse.swt.events.MouseTrackAdapter(){
                public void mouseExit( org.eclipse.swt.events.MouseEvent e )
                {
                    if (tipShell.isVisible()) {
                        tipShell.setVisible( false );
                    }
                    tipWidget = null;
                }

                public void mouseHover( org.eclipse.swt.events.MouseEvent event )
                {
                    org.eclipse.swt.graphics.Point pt = new org.eclipse.swt.graphics.Point( event.x, event.y );
                    org.eclipse.swt.widgets.Widget widget = event.widget;
                    if (widget instanceof org.eclipse.swt.widgets.ToolBar) {
                        org.eclipse.swt.widgets.ToolBar w = (org.eclipse.swt.widgets.ToolBar) widget;
                        widget = w.getItem( pt );
                    }
                    if (widget instanceof org.eclipse.swt.widgets.Table) {
                        org.eclipse.swt.widgets.Table w = (org.eclipse.swt.widgets.Table) widget;
                        widget = w.getItem( pt );
                    }
                    if (widget instanceof org.eclipse.swt.widgets.Tree) {
                        org.eclipse.swt.widgets.Tree w = (org.eclipse.swt.widgets.Tree) widget;
                        widget = w.getItem( pt );
                    }
                    if (widget == null) {
                        tipShell.setVisible( false );
                        tipWidget = null;
                        return;
                    }
                    if (widget == tipWidget) {
                        return;
                    }
                    tipWidget = widget;
                    tipPosition = control.toDisplay( pt );
                    java.lang.String text = (java.lang.String) widget.getData( "TIP_TEXT" );
                    org.eclipse.swt.graphics.Image image = (org.eclipse.swt.graphics.Image) widget.getData( "TIP_IMAGE" );
                    tipLabelText.setText( text != null ? text : "" );
                    tipLabelImage.setImage( image );
                    tipShell.pack();
                    setHoverLocation( tipShell, tipPosition );
                    tipShell.setVisible( true );
                }
            } );
            control.addHelpListener( new org.eclipse.swt.events.HelpListener(){
                public void helpRequested( org.eclipse.swt.events.HelpEvent event )
                {
                    if (tipWidget == null) {
                        return;
                    }
                    org.eclipse.swt.examples.hoverhelp.HoverHelp.ToolTipHelpTextHandler handler = (org.eclipse.swt.examples.hoverhelp.HoverHelp.ToolTipHelpTextHandler) tipWidget.getData( "TIP_HELPTEXTHANDLER" );
                    if (handler == null) {
                        return;
                    }
                    java.lang.String text = handler.getHelpText( tipWidget );
                    if (text == null) {
                        return;
                    }
                    if (tipShell.isVisible()) {
                        tipShell.setVisible( false );
                        org.eclipse.swt.widgets.Shell helpShell = new org.eclipse.swt.widgets.Shell( parentShell, SWT.SHELL_TRIM );
                        helpShell.setLayout( new org.eclipse.swt.layout.FillLayout() );
                        org.eclipse.swt.widgets.Label label = new org.eclipse.swt.widgets.Label( helpShell, SWT.NONE );
                        label.setText( text );
                        helpShell.pack();
                        setHoverLocation( helpShell, tipPosition );
                        helpShell.open();
                    }
                }
            } );
        }

        private void setHoverLocation( org.eclipse.swt.widgets.Shell shell, org.eclipse.swt.graphics.Point position )
        {
            org.eclipse.swt.graphics.Rectangle displayBounds = shell.getDisplay().getBounds();
            org.eclipse.swt.graphics.Rectangle shellBounds = shell.getBounds();
            shellBounds.x = Math.max( Math.min( position.x, displayBounds.width - shellBounds.width ), 0 );
            shellBounds.y = Math.max( Math.min( position.y + 16, displayBounds.height - shellBounds.height ), 0 );
            shell.setBounds( shellBounds );
        }

    }

    protected interface ToolTipHelpTextHandler
    {

        public java.lang.String getHelpText( org.eclipse.swt.widgets.Widget widget );

    }

}
