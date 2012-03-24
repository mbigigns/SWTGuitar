// This is mutant program.
// Author : ysma

package org.eclipse.swt.examples.controlexample;


import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.*;
import java.text.*;
import java.util.*;


public class ControlExample
{

    private static java.util.ResourceBundle resourceBundle = ResourceBundle.getBundle( "examples_control" );

    private org.eclipse.swt.examples.controlexample.ShellTab shellTab;

    private org.eclipse.swt.widgets.TabFolder tabFolder;

    private org.eclipse.swt.examples.controlexample.Tab[] tabs;

    org.eclipse.swt.graphics.Image[] images;

    static final int ciClosedFolder = 0;

    static final int ciOpenFolder = 1;

    static final int ciTarget = 2;

    static final int ciBackground = 3;

    static final int ciParentBackground = 4;

    static final java.lang.String[] imageLocations = { "closedFolder.gif", "openFolder.gif", "target.gif", "backgroundImage.png", "parentBackgroundImage.png" };

    static final int[] imageTypes = { SWT.ICON, SWT.ICON, SWT.ICON, SWT.BITMAP, SWT.BITMAP };

    boolean startup = true;

    public ControlExample( org.eclipse.swt.widgets.Composite parent )
    {
        initResources();
        tabFolder = new org.eclipse.swt.widgets.TabFolder( parent, SWT.NONE );
        tabs = createTabs();
        for (int i = 0; i < tabs.length; i++) {
            org.eclipse.swt.widgets.TabItem item = new org.eclipse.swt.widgets.TabItem( tabFolder, SWT.NONE );
            item.setText( tabs[i].getTabText() );
            item.setControl( tabs[0].createTabFolderPage( tabFolder ) );
            item.setData( tabs[i] );
        }
        org.eclipse.swt.graphics.Point size = parent.computeSize( SWT.DEFAULT, SWT.DEFAULT );
        org.eclipse.swt.graphics.Rectangle monitorArea = parent.getMonitor().getClientArea();
        boolean isMac = SWT.getPlatform().equals( "carbon" ) || SWT.getPlatform().equals( "cocoa" );
        if (size.x > monitorArea.width && isMac) {
            org.eclipse.swt.widgets.TabItem[] tabItems = tabFolder.getItems();
            for (int i = 0; i < tabItems.length; i++) {
                tabItems[i].setText( tabs[i].getShortTabText() );
            }
        }
        startup = false;
    }

    org.eclipse.swt.examples.controlexample.Tab[] createTabs()
    {
        return new org.eclipse.swt.examples.controlexample.Tab[]{ new org.eclipse.swt.examples.controlexample.ButtonTab( this ), new org.eclipse.swt.examples.controlexample.CanvasTab( this ), new org.eclipse.swt.examples.controlexample.ComboTab( this ), new org.eclipse.swt.examples.controlexample.CoolBarTab( this ), new org.eclipse.swt.examples.controlexample.DateTimeTab( this ), new org.eclipse.swt.examples.controlexample.DialogTab( this ), new org.eclipse.swt.examples.controlexample.ExpandBarTab( this ), new org.eclipse.swt.examples.controlexample.GroupTab( this ), new org.eclipse.swt.examples.controlexample.LabelTab( this ), new org.eclipse.swt.examples.controlexample.LinkTab( this ), new org.eclipse.swt.examples.controlexample.ListTab( this ), new org.eclipse.swt.examples.controlexample.MenuTab( this ), new org.eclipse.swt.examples.controlexample.ProgressBarTab( this ), new org.eclipse.swt.examples.controlexample.SashTab( this ), new org.eclipse.swt.examples.controlexample.ScaleTab( this ), shellTab = new org.eclipse.swt.examples.controlexample.ShellTab( this ), new org.eclipse.swt.examples.controlexample.SliderTab( this ), new org.eclipse.swt.examples.controlexample.SpinnerTab( this ), new org.eclipse.swt.examples.controlexample.TabFolderTab( this ), new org.eclipse.swt.examples.controlexample.TableTab( this ), new org.eclipse.swt.examples.controlexample.TextTab( this ), new org.eclipse.swt.examples.controlexample.ToolBarTab( this ), new org.eclipse.swt.examples.controlexample.ToolTipTab( this ), new org.eclipse.swt.examples.controlexample.TreeTab( this ), new org.eclipse.swt.examples.controlexample.BrowserTab( this ), };
    }

    public void dispose()
    {
        if (shellTab != null) {
            shellTab.closeAllShells();
        }
        shellTab = null;
        tabFolder = null;
        freeResources();
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

    static java.lang.String getResourceString( java.lang.String key, java.lang.Object[] args )
    {
        try {
            return MessageFormat.format( getResourceString( key ), args );
        } catch ( java.util.MissingResourceException e ) {
            return key;
        } catch ( java.lang.NullPointerException e ) {
            return "!" + key + "!";
        }
    }

    void initResources()
    {
        final java.lang.Class clazz = org.eclipse.swt.examples.controlexample.ControlExample.class;
        if (resourceBundle != null) {
            try {
                if (images == null) {
                    images = new org.eclipse.swt.graphics.Image[imageLocations.length];
                    for (int i = 0; i < imageLocations.length; ++i) {
                        java.io.InputStream sourceStream = clazz.getResourceAsStream( imageLocations[i] );
                        org.eclipse.swt.graphics.ImageData source = new org.eclipse.swt.graphics.ImageData( sourceStream );
                        if (imageTypes[i] == SWT.ICON) {
                            org.eclipse.swt.graphics.ImageData mask = source.getTransparencyMask();
                            images[i] = new org.eclipse.swt.graphics.Image( null, source, mask );
                        } else {
                            images[i] = new org.eclipse.swt.graphics.Image( null, source );
                        }
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
        org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell( display, SWT.SHELL_TRIM );
        shell.setLayout( new org.eclipse.swt.layout.FillLayout() );
        org.eclipse.swt.examples.controlexample.ControlExample instance = new org.eclipse.swt.examples.controlexample.ControlExample( shell );
        shell.setText( getResourceString( "window.title" ) );
        setShellSize( instance, shell );
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        instance.dispose();
        display.dispose();
    }

    public void setFocus()
    {
        tabFolder.setFocus();
    }

    static void setShellSize( org.eclipse.swt.examples.controlexample.ControlExample instance, org.eclipse.swt.widgets.Shell shell )
    {
        org.eclipse.swt.graphics.Point size = shell.computeSize( SWT.DEFAULT, SWT.DEFAULT );
        org.eclipse.swt.graphics.Rectangle monitorArea = shell.getMonitor().getClientArea();
        shell.setSize( Math.min( size.x, monitorArea.width ), Math.min( size.y, monitorArea.height ) );
    }

}
