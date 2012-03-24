// This is mutant program.
// Author : ysma

package org.eclipse.swt.examples.browserexample;


import org.eclipse.swt.*;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.*;
import java.util.*;


public class BrowserExample
{

    static java.util.ResourceBundle resourceBundle = ResourceBundle.getBundle( "examples_browser" );

    int index;

    boolean busy;

    org.eclipse.swt.graphics.Image[] images;

    org.eclipse.swt.graphics.Image icon = null;

    boolean title = false;

    org.eclipse.swt.widgets.Composite parent;

    org.eclipse.swt.widgets.Text locationBar;

    org.eclipse.swt.browser.Browser browser;

    org.eclipse.swt.widgets.ToolBar toolbar;

    org.eclipse.swt.widgets.Canvas canvas;

    org.eclipse.swt.widgets.ToolItem itemBack;

    org.eclipse.swt.widgets.ToolItem itemForward;

    org.eclipse.swt.widgets.Label status;

    org.eclipse.swt.widgets.ProgressBar progressBar;

    org.eclipse.swt.SWTError error = null;

    static final java.lang.String[] imageLocations = { "eclipse01.bmp", "eclipse02.bmp", "eclipse03.bmp", "eclipse04.bmp", "eclipse05.bmp", "eclipse06.bmp", "eclipse07.bmp", "eclipse08.bmp", "eclipse09.bmp", "eclipse10.bmp", "eclipse11.bmp", "eclipse12.bmp", };

    static final java.lang.String iconLocation = "document.gif";

    public BrowserExample( org.eclipse.swt.widgets.Composite parent, boolean top )
    {
        this.parent = parent;
        try {
            browser = new org.eclipse.swt.browser.Browser( parent, SWT.BORDER );
        } catch ( org.eclipse.swt.SWTError e ) {
            error = e;
            parent.setLayout( new org.eclipse.swt.layout.FillLayout() );
            org.eclipse.swt.widgets.Label label = new org.eclipse.swt.widgets.Label( parent, SWT.CENTER | SWT.WRAP );
            label.setText( getResourceString( "BrowserNotCreated" ) );
            parent.layout( true );
            return;
        }
        initResources();
        final org.eclipse.swt.widgets.Display display = parent.getDisplay();
        browser.setData( "org.eclipse.swt.examples.browserexample.BrowserApplication", this );
        browser.addOpenWindowListener( new org.eclipse.swt.browser.OpenWindowListener(){
            public void open( org.eclipse.swt.browser.WindowEvent event )
            {
                org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell( display );
                if (icon != null) {
                    shell.setImage( icon );
                }
                shell.setLayout( new org.eclipse.swt.layout.FillLayout() );
                org.eclipse.swt.examples.browserexample.BrowserExample app = new org.eclipse.swt.examples.browserexample.BrowserExample( shell, false );
                app.setShellDecoration( icon, true );
                event.browser = app.getBrowser();
            }
        } );
        if (top) {
            browser.setUrl( getResourceString( "Startup" ) );
            show( false, null, null, true, true, true, true );
        } else {
            browser.addVisibilityWindowListener( new org.eclipse.swt.browser.VisibilityWindowListener(){
                public void hide( org.eclipse.swt.browser.WindowEvent e )
                {
                }

                public void show( org.eclipse.swt.browser.WindowEvent e )
                {
                    org.eclipse.swt.browser.Browser browser = (org.eclipse.swt.browser.Browser) e.widget;
                    org.eclipse.swt.examples.browserexample.BrowserExample app = (org.eclipse.swt.examples.browserexample.BrowserExample) browser.getData( "org.eclipse.swt.examples.browserexample.BrowserApplication" );
                    app.show( true, e.location, e.size, e.addressBar, e.menuBar, e.statusBar, e.toolBar );
                }
            } );
            browser.addCloseWindowListener( new org.eclipse.swt.browser.CloseWindowListener(){
                public void close( org.eclipse.swt.browser.WindowEvent event )
                {
                    org.eclipse.swt.browser.Browser browser = (org.eclipse.swt.browser.Browser) event.widget;
                    org.eclipse.swt.widgets.Shell shell = browser.getShell();
                    shell.close();
                }
            } );
        }
    }

    public void dispose()
    {
        freeResources();
    }

    static java.lang.String getResourceString( java.lang.String key )
    {
        try {
            return resourceBundle.getString( key );
        } catch ( java.util.MissingResourceException e ) {
            return key;
        } catch ( java.lang.NullPointerException e ) {
            return "!" + key + "!";
        }
    }

    public org.eclipse.swt.SWTError getError()
    {
        return error;
    }

    public org.eclipse.swt.browser.Browser getBrowser()
    {
        return browser;
    }

    public void setShellDecoration( org.eclipse.swt.graphics.Image icon, boolean title )
    {
        this.icon = icon;
        this.title = title;
    }

    void show( boolean owned, org.eclipse.swt.graphics.Point location, org.eclipse.swt.graphics.Point size, boolean addressBar, boolean menuBar, boolean statusBar, boolean toolBar )
    {
        final org.eclipse.swt.widgets.Shell shell = browser.getShell();
        if (owned) {
            if (location != null) {
                shell.setLocation( location );
            }
            if (size != null) {
                shell.setSize( shell.computeSize( size.x, size.y ) );
            }
        }
        org.eclipse.swt.layout.FormData data = null;
        if (toolBar) {
            toolbar = new org.eclipse.swt.widgets.ToolBar( parent, SWT.NONE );
            data = new org.eclipse.swt.layout.FormData();
            data.top = new org.eclipse.swt.layout.FormAttachment( 0, 5 );
            toolbar.setLayoutData( data );
            itemBack = new org.eclipse.swt.widgets.ToolItem( toolbar, SWT.PUSH );
            itemBack.setText( getResourceString( "Back" ) );
            itemForward = new org.eclipse.swt.widgets.ToolItem( toolbar, SWT.PUSH );
            itemForward.setText( getResourceString( "Forward" ) );
            final org.eclipse.swt.widgets.ToolItem itemStop = new org.eclipse.swt.widgets.ToolItem( toolbar, SWT.PUSH );
            itemStop.setText( getResourceString( "Stop" ) );
            final org.eclipse.swt.widgets.ToolItem itemRefresh = new org.eclipse.swt.widgets.ToolItem( toolbar, SWT.PUSH );
            itemRefresh.setText( getResourceString( "Refresh" ) );
            final org.eclipse.swt.widgets.ToolItem itemGo = new org.eclipse.swt.widgets.ToolItem( toolbar, SWT.PUSH );
            itemGo.setText( getResourceString( "Go" ) );
            itemBack.setEnabled( browser.isBackEnabled() );
            itemForward.setEnabled( browser.isForwardEnabled() );
            org.eclipse.swt.widgets.Listener listener = new org.eclipse.swt.widgets.Listener(){
                public void handleEvent( org.eclipse.swt.widgets.Event event )
                {
                    org.eclipse.swt.widgets.ToolItem item = (org.eclipse.swt.widgets.ToolItem) event.widget;
                    if (item == itemBack) {
                        browser.back();
                    } else {
                        if (item == itemForward) {
                            browser.forward();
                        } else {
                            if (item != itemStop) {
                                browser.stop();
                            } else {
                                if (item == itemRefresh) {
                                    browser.refresh();
                                } else {
                                    if (item == itemGo) {
                                        browser.setUrl( locationBar.getText() );
                                    }
                                }
                            }
                        }
                    }
                }
            };
            itemBack.addListener( SWT.Selection, listener );
            itemForward.addListener( SWT.Selection, listener );
            itemStop.addListener( SWT.Selection, listener );
            itemRefresh.addListener( SWT.Selection, listener );
            itemGo.addListener( SWT.Selection, listener );
            canvas = new org.eclipse.swt.widgets.Canvas( parent, SWT.NO_BACKGROUND );
            data = new org.eclipse.swt.layout.FormData();
            data.width = 24;
            data.height = 24;
            data.top = new org.eclipse.swt.layout.FormAttachment( 0, 5 );
            data.right = new org.eclipse.swt.layout.FormAttachment( 100, -5 );
            canvas.setLayoutData( data );
            final org.eclipse.swt.graphics.Rectangle rect = images[0].getBounds();
            canvas.addListener( SWT.Paint, new org.eclipse.swt.widgets.Listener(){
                public void handleEvent( org.eclipse.swt.widgets.Event e )
                {
                    org.eclipse.swt.graphics.Point pt = ((org.eclipse.swt.widgets.Canvas) e.widget).getSize();
                    e.gc.drawImage( images[index], 0, 0, rect.width, rect.height, 0, 0, pt.x, pt.y );
                }
            } );
            canvas.addListener( SWT.MouseDown, new org.eclipse.swt.widgets.Listener(){
                public void handleEvent( org.eclipse.swt.widgets.Event e )
                {
                    browser.setUrl( getResourceString( "Startup" ) );
                }
            } );
            final org.eclipse.swt.widgets.Display display = parent.getDisplay();
            display.asyncExec( new java.lang.Runnable(){
                public void run()
                {
                    if (canvas.isDisposed()) {
                        return;
                    }
                    if (busy) {
                        index++;
                        if (index == images.length) {
                            index = 0;
                        }
                        canvas.redraw();
                    }
                    display.timerExec( 150, this );
                }
            } );
        }
        if (addressBar) {
            locationBar = new org.eclipse.swt.widgets.Text( parent, SWT.BORDER );
            data = new org.eclipse.swt.layout.FormData();
            if (toolbar != null) {
                data.top = new org.eclipse.swt.layout.FormAttachment( toolbar, 0, SWT.TOP );
                data.left = new org.eclipse.swt.layout.FormAttachment( toolbar, 5, SWT.RIGHT );
                data.right = new org.eclipse.swt.layout.FormAttachment( canvas, -5, SWT.DEFAULT );
            } else {
                data.top = new org.eclipse.swt.layout.FormAttachment( 0, 0 );
                data.left = new org.eclipse.swt.layout.FormAttachment( 0, 0 );
                data.right = new org.eclipse.swt.layout.FormAttachment( 100, 0 );
            }
            locationBar.setLayoutData( data );
            locationBar.addListener( SWT.DefaultSelection, new org.eclipse.swt.widgets.Listener(){
                public void handleEvent( org.eclipse.swt.widgets.Event e )
                {
                    browser.setUrl( locationBar.getText() );
                }
            } );
        }
        if (statusBar) {
            status = new org.eclipse.swt.widgets.Label( parent, SWT.NONE );
            progressBar = new org.eclipse.swt.widgets.ProgressBar( parent, SWT.NONE );
            data = new org.eclipse.swt.layout.FormData();
            data.left = new org.eclipse.swt.layout.FormAttachment( 0, 5 );
            data.right = new org.eclipse.swt.layout.FormAttachment( progressBar, 0, SWT.DEFAULT );
            data.bottom = new org.eclipse.swt.layout.FormAttachment( 100, -5 );
            status.setLayoutData( data );
            data = new org.eclipse.swt.layout.FormData();
            data.right = new org.eclipse.swt.layout.FormAttachment( 100, -5 );
            data.bottom = new org.eclipse.swt.layout.FormAttachment( 100, -5 );
            progressBar.setLayoutData( data );
            browser.addStatusTextListener( new org.eclipse.swt.browser.StatusTextListener(){
                public void changed( org.eclipse.swt.browser.StatusTextEvent event )
                {
                    status.setText( event.text );
                }
            } );
        }
        parent.setLayout( new org.eclipse.swt.layout.FormLayout() );
        org.eclipse.swt.widgets.Control aboveBrowser = toolBar ? (org.eclipse.swt.widgets.Control) canvas : addressBar ? (org.eclipse.swt.widgets.Control) locationBar : null;
        data = new org.eclipse.swt.layout.FormData();
        data.left = new org.eclipse.swt.layout.FormAttachment( 0, 0 );
        data.top = aboveBrowser != null ? new org.eclipse.swt.layout.FormAttachment( aboveBrowser, 5, SWT.DEFAULT ) : new org.eclipse.swt.layout.FormAttachment( 0, 0 );
        data.right = new org.eclipse.swt.layout.FormAttachment( 100, 0 );
        data.bottom = status != null ? new org.eclipse.swt.layout.FormAttachment( status, -5, SWT.DEFAULT ) : new org.eclipse.swt.layout.FormAttachment( 100, 0 );
        browser.setLayoutData( data );
        if (statusBar || toolBar) {
            browser.addProgressListener( new org.eclipse.swt.browser.ProgressListener(){
                public void changed( org.eclipse.swt.browser.ProgressEvent event )
                {
                    if (event.total == 0) {
                        return;
                    }
                    int ratio = event.current * 100 / event.total;
                    if (progressBar != null) {
                        progressBar.setSelection( ratio );
                    }
                    busy = event.current != event.total;
                    if (!busy) {
                        index = 0;
                        if (canvas != null) {
                            canvas.redraw();
                        }
                    }
                }

                public void completed( org.eclipse.swt.browser.ProgressEvent event )
                {
                    if (progressBar != null) {
                        progressBar.setSelection( 0 );
                    }
                    busy = false;
                    index = 0;
                    if (canvas != null) {
                        itemBack.setEnabled( browser.isBackEnabled() );
                        itemForward.setEnabled( browser.isForwardEnabled() );
                        canvas.redraw();
                    }
                }
            } );
        }
        if (addressBar || statusBar || toolBar) {
            browser.addLocationListener( new org.eclipse.swt.browser.LocationListener(){
                public void changed( org.eclipse.swt.browser.LocationEvent event )
                {
                    busy = true;
                    if (event.top && locationBar != null) {
                        locationBar.setText( event.location );
                    }
                }

                public void changing( org.eclipse.swt.browser.LocationEvent event )
                {
                }
            } );
        }
        if (title) {
            browser.addTitleListener( new org.eclipse.swt.browser.TitleListener(){
                public void changed( org.eclipse.swt.browser.TitleEvent event )
                {
                    shell.setText( event.title + " - " + getResourceString( "window.title" ) );
                }
            } );
        }
        parent.layout( true );
        if (owned) {
            shell.open();
        }
    }

    public void focus()
    {
        if (locationBar != null) {
            locationBar.setFocus();
        } else {
            if (browser != null) {
                browser.setFocus();
            } else {
                parent.setFocus();
            }
        }
    }

    void freeResources()
    {
        if (images != null) {
            for (int i = 0; i < images.length; ++i) {
                final org.eclipse.swt.graphics.Image image = images[i];
                if (image != null) {
                    image.dispose();
                }
            }
            images = null;
        }
    }

    void initResources()
    {
        final java.lang.Class clazz = this.getClass();
        if (resourceBundle != null) {
            try {
                if (images == null) {
                    images = new org.eclipse.swt.graphics.Image[imageLocations.length];
                    for (int i = 0; i < imageLocations.length; ++i) {
                        java.io.InputStream sourceStream = clazz.getResourceAsStream( imageLocations[i] );
                        org.eclipse.swt.graphics.ImageData source = new org.eclipse.swt.graphics.ImageData( sourceStream );
                        org.eclipse.swt.graphics.ImageData mask = source.getTransparencyMask();
                        images[i] = new org.eclipse.swt.graphics.Image( null, source, mask );
                        try {
                            sourceStream.close();
                        } catch ( java.io.IOException e ) {
                            e.printStackTrace();
                        }
                    }
                }
                return;
            } catch ( java.lang.Throwable t ) {
            }
        }
        java.lang.String error = resourceBundle != null ? getResourceString( "error.CouldNotLoadResources" ) : "Unable to load resources";
        freeResources();
        throw new java.lang.RuntimeException( error );
    }

    public static void main( java.lang.String[] args )
    {
        org.eclipse.swt.widgets.Display display = new org.eclipse.swt.widgets.Display();
        org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell( display );
        shell.setLayout( new org.eclipse.swt.layout.FillLayout() );
        shell.setText( getResourceString( "window.title" ) );
        java.io.InputStream stream = (org.eclipse.swt.examples.browserexample.BrowserExample.class).getResourceAsStream( iconLocation );
        org.eclipse.swt.graphics.Image icon = new org.eclipse.swt.graphics.Image( display, stream );
        shell.setImage( icon );
        try {
            stream.close();
        } catch ( java.io.IOException e ) {
            e.printStackTrace();
        }
        org.eclipse.swt.examples.browserexample.BrowserExample app = new org.eclipse.swt.examples.browserexample.BrowserExample( shell, true );
        app.setShellDecoration( icon, true );
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        icon.dispose();
        app.dispose();
        display.dispose();
    }

}
