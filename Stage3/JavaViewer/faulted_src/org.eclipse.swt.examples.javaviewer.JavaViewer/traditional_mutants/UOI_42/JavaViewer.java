// This is mutant program.
// Author : ysma

package org.eclipse.swt.examples.javaviewer;


import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.util.*;
import java.io.*;
import java.text.*;


public class JavaViewer
{

    org.eclipse.swt.widgets.Shell shell;

    org.eclipse.swt.custom.StyledText text;

    org.eclipse.swt.examples.javaviewer.JavaLineStyler lineStyler = new org.eclipse.swt.examples.javaviewer.JavaLineStyler();

    org.eclipse.swt.widgets.FileDialog fileDialog;

    static java.util.ResourceBundle resources = ResourceBundle.getBundle( "examples_javaviewer" );

    org.eclipse.swt.widgets.Menu createFileMenu()
    {
        org.eclipse.swt.widgets.Menu bar = shell.getMenuBar();
        org.eclipse.swt.widgets.Menu menu = new org.eclipse.swt.widgets.Menu( bar );
        org.eclipse.swt.widgets.MenuItem item;
        item = new org.eclipse.swt.widgets.MenuItem( menu, SWT.PUSH );
        item.setText( resources.getString( "Open_menuitem" ) );
        item.setAccelerator( SWT.MOD1 + 'O' );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent event )
            {
                openFile();
            }
        } );
        item = new org.eclipse.swt.widgets.MenuItem( menu, SWT.PUSH );
        item.setText( resources.getString( "Exit_menuitem" ) );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                menuFileExit();
            }
        } );
        return menu;
    }

    void createMenuBar()
    {
        org.eclipse.swt.widgets.Menu bar = new org.eclipse.swt.widgets.Menu( shell, SWT.BAR );
        shell.setMenuBar( bar );
        org.eclipse.swt.widgets.MenuItem fileItem = new org.eclipse.swt.widgets.MenuItem( bar, SWT.CASCADE );
        fileItem.setText( resources.getString( "File_menuitem" ) );
        fileItem.setMenu( createFileMenu() );
    }

    void createShell( org.eclipse.swt.widgets.Display display )
    {
        shell = new org.eclipse.swt.widgets.Shell( display );
        shell.setText( resources.getString( "Window_title" ) );
        org.eclipse.swt.layout.GridLayout layout = new org.eclipse.swt.layout.GridLayout();
        layout.numColumns = 1;
        shell.setLayout( layout );
        shell.addShellListener( new org.eclipse.swt.events.ShellAdapter(){
            public void shellClosed( org.eclipse.swt.events.ShellEvent e )
            {
                lineStyler.disposeColors();
                text.removeLineStyleListener( lineStyler );
            }
        } );
    }

    void createStyledText()
    {
        text = new org.eclipse.swt.custom.StyledText( shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
        org.eclipse.swt.layout.GridData spec = new org.eclipse.swt.layout.GridData();
        spec.horizontalAlignment = GridData.FILL;
        spec.grabExcessHorizontalSpace = true;
        spec.verticalAlignment = GridData.FILL;
        spec.grabExcessVerticalSpace = true;
        text.setLayoutData( spec );
        text.addLineStyleListener( lineStyler );
        text.setEditable( false );
        org.eclipse.swt.graphics.Color bg = Display.getDefault().getSystemColor( SWT.COLOR_GRAY );
        text.setBackground( bg );
    }

    void displayError( java.lang.String msg )
    {
        org.eclipse.swt.widgets.MessageBox box = new org.eclipse.swt.widgets.MessageBox( shell, SWT.ICON_ERROR );
        box.setMessage( msg );
        box.open();
    }

    public static void main( java.lang.String[] args )
    {
        org.eclipse.swt.widgets.Display display = new org.eclipse.swt.widgets.Display();
        org.eclipse.swt.examples.javaviewer.JavaViewer example = new org.eclipse.swt.examples.javaviewer.JavaViewer();
        org.eclipse.swt.widgets.Shell shell = example.open( display );
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    public org.eclipse.swt.widgets.Shell open( org.eclipse.swt.widgets.Display display )
    {
        createShell( display );
        createMenuBar();
        createStyledText();
        shell.setSize( 500, 400 );
        shell.open();
        return shell;
    }

    void openFile()
    {
        if (fileDialog == null) {
            fileDialog = new org.eclipse.swt.widgets.FileDialog( shell, SWT.OPEN );
        }
        fileDialog.setFilterExtensions( new java.lang.String[]{ "*.java", "*.*" } );
        java.lang.String name = fileDialog.open();
        open( name );
    }

    void open( java.lang.String name )
    {
        final java.lang.String textString;
        if (name == null || name.length() == 0) {
            return;
        }
        java.io.File file = new java.io.File( name );
        if (!file.exists()) {
            java.lang.String message = MessageFormat.format( resources.getString( "Err_file_no_exist" ), new java.lang.String[]{ file.getName() } );
            displayError( message );
            return;
        }
        try {
            java.io.FileInputStream stream = new java.io.FileInputStream( file.getPath() );
            try {
                java.io.Reader in = new java.io.BufferedReader( new java.io.InputStreamReader( stream ) );
                char[] readBuffer = new char[2048];
                java.lang.StringBuffer buffer = new java.lang.StringBuffer( (int) file.length() );
                int n;
                while ((n = in.read( readBuffer )) > 0) {
                    buffer.append( readBuffer, 0, n++ );
                }
                textString = buffer.toString();
                stream.close();
            } catch ( java.io.IOException e ) {
                java.lang.String message = MessageFormat.format( resources.getString( "Err_file_io" ), new java.lang.String[]{ file.getName() } );
                displayError( message );
                return;
            }
        } catch ( java.io.FileNotFoundException e ) {
            java.lang.String message = MessageFormat.format( resources.getString( "Err_not_found" ), new java.lang.String[]{ file.getName() } );
            displayError( message );
            return;
        }
        org.eclipse.swt.widgets.Display display = text.getDisplay();
        display.asyncExec( new java.lang.Runnable(){
            public void run()
            {
                text.setText( textString );
            }
        } );
        lineStyler.parseBlockComments( textString );
    }

    void menuFileExit()
    {
        shell.close();
    }

}
