// This is mutant program.
// Author : ysma

package org.eclipse.swt.examples.layoutexample;


import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import java.text.*;
import java.util.*;


public class LayoutExample
{

    private static java.util.ResourceBundle resourceBundle = ResourceBundle.getBundle( "examples_layout" );

    private org.eclipse.swt.widgets.TabFolder tabFolder;

    public LayoutExample( org.eclipse.swt.widgets.Composite parent )
    {
        tabFolder = new org.eclipse.swt.widgets.TabFolder( parent, SWT.NONE );
        org.eclipse.swt.examples.layoutexample.Tab[] tabs = new org.eclipse.swt.examples.layoutexample.Tab[]{ new org.eclipse.swt.examples.layoutexample.FillLayoutTab( this ), new org.eclipse.swt.examples.layoutexample.RowLayoutTab( this ), new org.eclipse.swt.examples.layoutexample.GridLayoutTab( this ), new org.eclipse.swt.examples.layoutexample.FormLayoutTab( this ), new org.eclipse.swt.examples.layoutexample.StackLayoutTab( this ) };
        for (int i = 0; i < tabs.length; i++) {
            org.eclipse.swt.widgets.TabItem item = new org.eclipse.swt.widgets.TabItem( tabFolder, SWT.NONE );
            item.setText( tabs[i].getTabText() );
            item.setControl( tabs[i].createTabFolderPage( tabFolder ) );
        }
    }

    public void setFocus()
    {
        tabFolder.setFocus();
    }

    public void dispose()
    {
        tabFolder = null;
    }

    public static void main( java.lang.String[] args )
    {
        final org.eclipse.swt.widgets.Display display = new org.eclipse.swt.widgets.Display();
        final org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell( display );
        shell.setLayout( new org.eclipse.swt.layout.FillLayout() );
        new org.eclipse.swt.examples.layoutexample.LayoutExample( shell );
        shell.setText( getResourceString( "window.title" ) );
        shell.addShellListener( new org.eclipse.swt.events.ShellAdapter(){
            public void shellClosed( org.eclipse.swt.events.ShellEvent e )
            {
                org.eclipse.swt.widgets.Shell[] shells = display.getShells();
                for (int i = 0; i < shells.length; i++) {
                    if (shells[i] != shell) {
                        shells[--i].close();
                    }
                }
            }
        } );
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
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

}
