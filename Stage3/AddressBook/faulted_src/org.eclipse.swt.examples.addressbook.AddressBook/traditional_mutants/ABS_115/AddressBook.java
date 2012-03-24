// This is mutant program.
// Author : ysma

package org.eclipse.swt.examples.addressbook;


import java.io.*;
import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class AddressBook
{

    private static java.util.ResourceBundle resAddressBook = ResourceBundle.getBundle( "examples_addressbook" );

    private org.eclipse.swt.widgets.Shell shell;

    private org.eclipse.swt.widgets.Table table;

    private org.eclipse.swt.examples.addressbook.SearchDialog searchDialog;

    private java.io.File file;

    private boolean isModified;

    private java.lang.String[] copyBuffer;

    private int lastSortColumn = -1;

    private static final java.lang.String DELIMITER = "\t";

    private static final java.lang.String[] columnNames = { resAddressBook.getString( "Last_name" ), resAddressBook.getString( "First_name" ), resAddressBook.getString( "Business_phone" ), resAddressBook.getString( "Home_phone" ), resAddressBook.getString( "Email" ), resAddressBook.getString( "Fax" ) };

    public static void main( java.lang.String[] args )
    {
        org.eclipse.swt.widgets.Display display = new org.eclipse.swt.widgets.Display();
        org.eclipse.swt.examples.addressbook.AddressBook application = new org.eclipse.swt.examples.addressbook.AddressBook();
        org.eclipse.swt.widgets.Shell shell = application.open( display );
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    public org.eclipse.swt.widgets.Shell open( org.eclipse.swt.widgets.Display display )
    {
        shell = new org.eclipse.swt.widgets.Shell( display );
        shell.setLayout( new org.eclipse.swt.layout.FillLayout() );
        shell.addShellListener( new org.eclipse.swt.events.ShellAdapter(){
            public void shellClosed( org.eclipse.swt.events.ShellEvent e )
            {
                e.doit = closeAddressBook();
            }
        } );
        createMenuBar();
        searchDialog = new org.eclipse.swt.examples.addressbook.SearchDialog( shell );
        searchDialog.setSearchAreaNames( columnNames );
        searchDialog.setSearchAreaLabel( resAddressBook.getString( "Column" ) );
        searchDialog.addFindListener( new org.eclipse.swt.examples.addressbook.FindListener(){
            public boolean find()
            {
                return findEntry();
            }
        } );
        table = new org.eclipse.swt.widgets.Table( shell, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION );
        table.setHeaderVisible( true );
        table.setMenu( createPopUpMenu() );
        table.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetDefaultSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.TableItem[] items = table.getSelection();
                if (items.length > 0) {
                    editEntry( items[0] );
                }
            }
        } );
        for (int i = 0; i < columnNames.length; i++) {
            org.eclipse.swt.widgets.TableColumn column = new org.eclipse.swt.widgets.TableColumn( table, SWT.NONE );
            column.setText( columnNames[i] );
            column.setWidth( 150 );
            final int columnIndex = i;
            column.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
                public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
                {
                    sort( columnIndex );
                }
            } );
        }
        newAddressBook();
        shell.setSize( table.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x, 300 );
        shell.open();
        return shell;
    }

    private boolean closeAddressBook()
    {
        if (isModified) {
            org.eclipse.swt.widgets.MessageBox box = new org.eclipse.swt.widgets.MessageBox( shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL );
            box.setText( shell.getText() );
            box.setMessage( resAddressBook.getString( "Close_save" ) );
            int choice = box.open();
            if (choice == SWT.CANCEL) {
                return false;
            } else {
                if (choice == SWT.YES) {
                    if (!save()) {
                        return false;
                    }
                }
            }
        }
        org.eclipse.swt.widgets.TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++) {
            items[i].dispose();
        }
        return true;
    }

    private org.eclipse.swt.widgets.Menu createMenuBar()
    {
        org.eclipse.swt.widgets.Menu menuBar = new org.eclipse.swt.widgets.Menu( shell, SWT.BAR );
        shell.setMenuBar( menuBar );
        createFileMenu( menuBar );
        createEditMenu( menuBar );
        createSearchMenu( menuBar );
        createHelpMenu( menuBar );
        return menuBar;
    }

    private java.lang.String[] decodeLine( java.lang.String line )
    {
        if (line == null) {
            return null;
        }
        java.lang.String[] parsedLine = new java.lang.String[table.getColumnCount()];
        for (int i = 0; i < parsedLine.length - 1; i++) {
            int index = line.indexOf( DELIMITER );
            if (index > -1) {
                parsedLine[i] = line.substring( 0, (-index) );
                line = line.substring( index + DELIMITER.length(), line.length() );
            } else {
                return null;
            }
        }
        if (line.indexOf( DELIMITER ) != -1) {
            return null;
        }
        parsedLine[parsedLine.length - 1] = line;
        return parsedLine;
    }

    private void displayError( java.lang.String msg )
    {
        org.eclipse.swt.widgets.MessageBox box = new org.eclipse.swt.widgets.MessageBox( shell, SWT.ICON_ERROR );
        box.setMessage( msg );
        box.open();
    }

    private void editEntry( org.eclipse.swt.widgets.TableItem item )
    {
        org.eclipse.swt.examples.addressbook.DataEntryDialog dialog = new org.eclipse.swt.examples.addressbook.DataEntryDialog( shell );
        dialog.setLabels( columnNames );
        java.lang.String[] values = new java.lang.String[table.getColumnCount()];
        for (int i = 0; i < values.length; i++) {
            values[i] = item.getText( i );
        }
        dialog.setValues( values );
        values = dialog.open();
        if (values != null) {
            item.setText( values );
            isModified = true;
        }
    }

    private java.lang.String encodeLine( java.lang.String[] tableItems )
    {
        java.lang.String line = "";
        for (int i = 0; i < tableItems.length - 1; i++) {
            line += tableItems[i] + DELIMITER;
        }
        line += tableItems[tableItems.length - 1] + "\n";
        return line;
    }

    private boolean findEntry()
    {
        org.eclipse.swt.graphics.Cursor waitCursor = shell.getDisplay().getSystemCursor( SWT.CURSOR_WAIT );
        shell.setCursor( waitCursor );
        boolean matchCase = searchDialog.getMatchCase();
        boolean matchWord = searchDialog.getMatchWord();
        java.lang.String searchString = searchDialog.getSearchString();
        int column = searchDialog.getSelectedSearchArea();
        searchString = matchCase ? searchString : searchString.toLowerCase();
        boolean found = false;
        if (searchDialog.getSearchDown()) {
            for (int i = table.getSelectionIndex() + 1; i < table.getItemCount(); i++) {
                if (found = findMatch( searchString, table.getItem( i ), column, matchWord, matchCase )) {
                    table.setSelection( i );
                    break;
                }
            }
        } else {
            for (int i = table.getSelectionIndex() - 1; i > -1; i--) {
                if (found = findMatch( searchString, table.getItem( i ), column, matchWord, matchCase )) {
                    table.setSelection( i );
                    break;
                }
            }
        }
        shell.setCursor( null );
        return found;
    }

    private boolean findMatch( java.lang.String searchString, org.eclipse.swt.widgets.TableItem item, int column, boolean matchWord, boolean matchCase )
    {
        java.lang.String tableText = matchCase ? item.getText( column ) : item.getText( column ).toLowerCase();
        if (matchWord) {
            if (tableText != null && tableText.equals( searchString )) {
                return true;
            }
        } else {
            if (tableText != null && tableText.indexOf( searchString ) != -1) {
                return true;
            }
        }
        return false;
    }

    private void newAddressBook()
    {
        shell.setText( resAddressBook.getString( "Title_bar" ) + resAddressBook.getString( "New_title" ) );
        file = null;
        isModified = false;
    }

    private void newEntry()
    {
        org.eclipse.swt.examples.addressbook.DataEntryDialog dialog = new org.eclipse.swt.examples.addressbook.DataEntryDialog( shell );
        dialog.setLabels( columnNames );
        java.lang.String[] data = dialog.open();
        if (data != null) {
            org.eclipse.swt.widgets.TableItem item = new org.eclipse.swt.widgets.TableItem( table, SWT.NONE );
            item.setText( data );
            isModified = true;
        }
    }

    private void openAddressBook()
    {
        org.eclipse.swt.widgets.FileDialog fileDialog = new org.eclipse.swt.widgets.FileDialog( shell, SWT.OPEN );
        fileDialog.setFilterExtensions( new java.lang.String[]{ "*.adr;", "*.*" } );
        fileDialog.setFilterNames( new java.lang.String[]{ resAddressBook.getString( "Book_filter_name" ) + " (*.adr)", resAddressBook.getString( "All_filter_name" ) + " (*.*)" } );
        java.lang.String name = fileDialog.open();
        if (name == null) {
            return;
        }
        java.io.File file = new java.io.File( name );
        if (!file.exists()) {
            displayError( resAddressBook.getString( "File" ) + file.getName() + " " + resAddressBook.getString( "Does_not_exist" ) );
            return;
        }
        org.eclipse.swt.graphics.Cursor waitCursor = shell.getDisplay().getSystemCursor( SWT.CURSOR_WAIT );
        shell.setCursor( waitCursor );
        java.io.FileReader fileReader = null;
        java.io.BufferedReader bufferedReader = null;
        java.lang.String[] data = new java.lang.String[0];
        try {
            fileReader = new java.io.FileReader( file.getAbsolutePath() );
            bufferedReader = new java.io.BufferedReader( fileReader );
            java.lang.String nextLine = bufferedReader.readLine();
            while (nextLine != null) {
                java.lang.String[] newData = new java.lang.String[data.length + 1];
                System.arraycopy( data, 0, newData, 0, data.length );
                newData[data.length] = nextLine;
                data = newData;
                nextLine = bufferedReader.readLine();
            }
        } catch ( java.io.FileNotFoundException e ) {
            displayError( resAddressBook.getString( "File_not_found" ) + "\n" + file.getName() );
            return;
        } catch ( java.io.IOException e ) {
            displayError( resAddressBook.getString( "IO_error_read" ) + "\n" + file.getName() );
            return;
        } finally 
{
            shell.setCursor( null );
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch ( java.io.IOException e ) {
                    displayError( resAddressBook.getString( "IO_error_close" ) + "\n" + file.getName() );
                    return;
                }
            }
        }
        java.lang.String[][] tableInfo = new java.lang.String[data.length][table.getColumnCount()];
        int writeIndex = 0;
        for (int i = 0; i < data.length; i++) {
            java.lang.String[] line = decodeLine( data[i] );
            if (line != null) {
                tableInfo[writeIndex++] = line;
            }
        }
        if (writeIndex != data.length) {
            java.lang.String[][] result = new java.lang.String[writeIndex][table.getColumnCount()];
            System.arraycopy( tableInfo, 0, result, 0, writeIndex );
            tableInfo = result;
        }
        Arrays.sort( tableInfo, new org.eclipse.swt.examples.addressbook.AddressBook.RowComparator( 0 ) );
        for (int i = 0; i < tableInfo.length; i++) {
            org.eclipse.swt.widgets.TableItem item = new org.eclipse.swt.widgets.TableItem( table, SWT.NONE );
            item.setText( tableInfo[i] );
        }
        shell.setText( resAddressBook.getString( "Title_bar" ) + fileDialog.getFileName() );
        isModified = false;
        this.file = file;
    }

    private boolean save()
    {
        if (file == null) {
            return saveAs();
        }
        org.eclipse.swt.graphics.Cursor waitCursor = new org.eclipse.swt.graphics.Cursor( shell.getDisplay(), SWT.CURSOR_WAIT );
        shell.setCursor( waitCursor );
        org.eclipse.swt.widgets.TableItem[] items = table.getItems();
        java.lang.String[] lines = new java.lang.String[items.length];
        for (int i = 0; i < items.length; i++) {
            java.lang.String[] itemText = new java.lang.String[table.getColumnCount()];
            for (int j = 0; j < itemText.length; j++) {
                itemText[j] = items[i].getText( j );
            }
            lines[i] = encodeLine( itemText );
        }
        java.io.FileWriter fileWriter = null;
        try {
            fileWriter = new java.io.FileWriter( file.getAbsolutePath(), false );
            for (int i = 0; i < lines.length; i++) {
                fileWriter.write( lines[i] );
            }
        } catch ( java.io.FileNotFoundException e ) {
            displayError( resAddressBook.getString( "File_not_found" ) + "\n" + file.getName() );
            return false;
        } catch ( java.io.IOException e ) {
            displayError( resAddressBook.getString( "IO_error_write" ) + "\n" + file.getName() );
            return false;
        } finally 
{
            shell.setCursor( null );
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch ( java.io.IOException e ) {
                    displayError( resAddressBook.getString( "IO_error_close" ) + "\n" + file.getName() );
                    return false;
                }
            }
        }
        shell.setText( resAddressBook.getString( "Title_bar" ) + file.getName() );
        isModified = false;
        return true;
    }

    private boolean saveAs()
    {
        org.eclipse.swt.widgets.FileDialog saveDialog = new org.eclipse.swt.widgets.FileDialog( shell, SWT.SAVE );
        saveDialog.setFilterExtensions( new java.lang.String[]{ "*.adr;", "*.*" } );
        saveDialog.setFilterNames( new java.lang.String[]{ "Address Books (*.adr)", "All Files " } );
        saveDialog.open();
        java.lang.String name = saveDialog.getFileName();
        if (name.equals( "" )) {
            return false;
        }
        if (name.indexOf( ".adr" ) != name.length() - 4) {
            name += ".adr";
        }
        java.io.File file = new java.io.File( saveDialog.getFilterPath(), name );
        if (file.exists()) {
            org.eclipse.swt.widgets.MessageBox box = new org.eclipse.swt.widgets.MessageBox( shell, SWT.ICON_WARNING | SWT.YES | SWT.NO );
            box.setText( resAddressBook.getString( "Save_as_title" ) );
            box.setMessage( resAddressBook.getString( "File" ) + file.getName() + " " + resAddressBook.getString( "Query_overwrite" ) );
            if (box.open() != SWT.YES) {
                return false;
            }
        }
        this.file = file;
        return save();
    }

    private void sort( int column )
    {
        if (table.getItemCount() <= 1) {
            return;
        }
        org.eclipse.swt.widgets.TableItem[] items = table.getItems();
        java.lang.String[][] data = new java.lang.String[items.length][table.getColumnCount()];
        for (int i = 0; i < items.length; i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                data[i][j] = items[i].getText( j );
            }
        }
        Arrays.sort( data, new org.eclipse.swt.examples.addressbook.AddressBook.RowComparator( column ) );
        if (lastSortColumn != column) {
            table.setSortColumn( table.getColumn( column ) );
            table.setSortDirection( SWT.DOWN );
            for (int i = 0; i < data.length; i++) {
                items[i].setText( data[i] );
            }
            lastSortColumn = column;
        } else {
            table.setSortDirection( SWT.UP );
            int j = data.length - 1;
            for (int i = 0; i < data.length; i++) {
                items[i].setText( data[j--] );
            }
            lastSortColumn = -1;
        }
    }

    private void createFileMenu( org.eclipse.swt.widgets.Menu menuBar )
    {
        org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem( menuBar, SWT.CASCADE );
        item.setText( resAddressBook.getString( "File_menu_title" ) );
        org.eclipse.swt.widgets.Menu menu = new org.eclipse.swt.widgets.Menu( shell, SWT.DROP_DOWN );
        item.setMenu( menu );
        menu.addMenuListener( new org.eclipse.swt.events.MenuAdapter(){
            public void menuShown( org.eclipse.swt.events.MenuEvent e )
            {
                org.eclipse.swt.widgets.Menu menu = (org.eclipse.swt.widgets.Menu) e.widget;
                org.eclipse.swt.widgets.MenuItem[] items = menu.getItems();
                items[1].setEnabled( table.getSelectionCount() != 0 );
                items[5].setEnabled( file != null && isModified );
                items[6].setEnabled( table.getItemCount() != 0 );
            }
        } );
        org.eclipse.swt.widgets.MenuItem subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "New_contact" ) );
        subItem.setAccelerator( SWT.MOD1 + 'N' );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                newEntry();
            }
        } );
        subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "Edit_contact" ) );
        subItem.setAccelerator( SWT.MOD1 + 'E' );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.TableItem[] items = table.getSelection();
                if (items.length == 0) {
                    return;
                }
                editEntry( items[0] );
            }
        } );
        new org.eclipse.swt.widgets.MenuItem( menu, SWT.SEPARATOR );
        subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "New_address_book" ) );
        subItem.setAccelerator( SWT.MOD1 + 'B' );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                if (closeAddressBook()) {
                    newAddressBook();
                }
            }
        } );
        subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "Open_address_book" ) );
        subItem.setAccelerator( SWT.MOD1 + 'O' );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                if (closeAddressBook()) {
                    openAddressBook();
                }
            }
        } );
        subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "Save_address_book" ) );
        subItem.setAccelerator( SWT.MOD1 + 'S' );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                save();
            }
        } );
        subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "Save_book_as" ) );
        subItem.setAccelerator( SWT.MOD1 + 'A' );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                saveAs();
            }
        } );
        new org.eclipse.swt.widgets.MenuItem( menu, SWT.SEPARATOR );
        subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "Exit" ) );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                shell.close();
            }
        } );
    }

    private org.eclipse.swt.widgets.MenuItem createEditMenu( org.eclipse.swt.widgets.Menu menuBar )
    {
        org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem( menuBar, SWT.CASCADE );
        item.setText( resAddressBook.getString( "Edit_menu_title" ) );
        org.eclipse.swt.widgets.Menu menu = new org.eclipse.swt.widgets.Menu( shell, SWT.DROP_DOWN );
        item.setMenu( menu );
        menu.addMenuListener( new org.eclipse.swt.events.MenuAdapter(){
            public void menuShown( org.eclipse.swt.events.MenuEvent e )
            {
                org.eclipse.swt.widgets.Menu menu = (org.eclipse.swt.widgets.Menu) e.widget;
                org.eclipse.swt.widgets.MenuItem[] items = menu.getItems();
                int count = table.getSelectionCount();
                items[0].setEnabled( count != 0 );
                items[1].setEnabled( count != 0 );
                items[2].setEnabled( copyBuffer != null );
                items[3].setEnabled( count != 0 );
                items[5].setEnabled( table.getItemCount() != 0 );
            }
        } );
        org.eclipse.swt.widgets.MenuItem subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.PUSH );
        subItem.setText( resAddressBook.getString( "Edit" ) );
        subItem.setAccelerator( SWT.MOD1 + 'E' );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.TableItem[] items = table.getSelection();
                if (items.length == 0) {
                    return;
                }
                editEntry( items[0] );
            }
        } );
        subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "Copy" ) );
        subItem.setAccelerator( SWT.MOD1 + 'C' );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.TableItem[] items = table.getSelection();
                if (items.length == 0) {
                    return;
                }
                copyBuffer = new java.lang.String[table.getColumnCount()];
                for (int i = 0; i < copyBuffer.length; i++) {
                    copyBuffer[i] = items[0].getText( i );
                }
            }
        } );
        subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "Paste" ) );
        subItem.setAccelerator( SWT.MOD1 + 'V' );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                if (copyBuffer == null) {
                    return;
                }
                org.eclipse.swt.widgets.TableItem item = new org.eclipse.swt.widgets.TableItem( table, SWT.NONE );
                item.setText( copyBuffer );
                isModified = true;
            }
        } );
        subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "Delete" ) );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.TableItem[] items = table.getSelection();
                if (items.length == 0) {
                    return;
                }
                items[0].dispose();
                isModified = true;
            }
        } );
        new org.eclipse.swt.widgets.MenuItem( menu, SWT.SEPARATOR );
        subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.CASCADE );
        subItem.setText( resAddressBook.getString( "Sort" ) );
        org.eclipse.swt.widgets.Menu submenu = createSortMenu();
        subItem.setMenu( submenu );
        return item;
    }

    private org.eclipse.swt.widgets.Menu createSortMenu()
    {
        org.eclipse.swt.widgets.Menu submenu = new org.eclipse.swt.widgets.Menu( shell, SWT.DROP_DOWN );
        org.eclipse.swt.widgets.MenuItem subitem;
        for (int i = 0; i < columnNames.length; i++) {
            subitem = new org.eclipse.swt.widgets.MenuItem( submenu, SWT.NONE );
            subitem.setText( columnNames[i] );
            final int column = i;
            subitem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
                public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
                {
                    sort( column );
                }
            } );
        }
        return submenu;
    }

    private void createSearchMenu( org.eclipse.swt.widgets.Menu menuBar )
    {
        org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem( menuBar, SWT.CASCADE );
        item.setText( resAddressBook.getString( "Search_menu_title" ) );
        org.eclipse.swt.widgets.Menu searchMenu = new org.eclipse.swt.widgets.Menu( shell, SWT.DROP_DOWN );
        item.setMenu( searchMenu );
        item = new org.eclipse.swt.widgets.MenuItem( searchMenu, SWT.NONE );
        item.setText( resAddressBook.getString( "Find" ) );
        item.setAccelerator( SWT.MOD1 + 'F' );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                searchDialog.setMatchCase( false );
                searchDialog.setMatchWord( false );
                searchDialog.setSearchDown( true );
                searchDialog.setSearchString( "" );
                searchDialog.setSelectedSearchArea( 0 );
                searchDialog.open();
            }
        } );
        item = new org.eclipse.swt.widgets.MenuItem( searchMenu, SWT.NONE );
        item.setText( resAddressBook.getString( "Find_next" ) );
        item.setAccelerator( SWT.F3 );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                searchDialog.open();
            }
        } );
    }

    private org.eclipse.swt.widgets.Menu createPopUpMenu()
    {
        org.eclipse.swt.widgets.Menu popUpMenu = new org.eclipse.swt.widgets.Menu( shell, SWT.POP_UP );
        popUpMenu.addMenuListener( new org.eclipse.swt.events.MenuAdapter(){
            public void menuShown( org.eclipse.swt.events.MenuEvent e )
            {
                org.eclipse.swt.widgets.Menu menu = (org.eclipse.swt.widgets.Menu) e.widget;
                org.eclipse.swt.widgets.MenuItem[] items = menu.getItems();
                int count = table.getSelectionCount();
                items[2].setEnabled( count != 0 );
                items[3].setEnabled( count != 0 );
                items[4].setEnabled( copyBuffer != null );
                items[5].setEnabled( count != 0 );
                items[7].setEnabled( table.getItemCount() != 0 );
            }
        } );
        org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem( popUpMenu, SWT.PUSH );
        item.setText( resAddressBook.getString( "Pop_up_new" ) );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                newEntry();
            }
        } );
        new org.eclipse.swt.widgets.MenuItem( popUpMenu, SWT.SEPARATOR );
        item = new org.eclipse.swt.widgets.MenuItem( popUpMenu, SWT.PUSH );
        item.setText( resAddressBook.getString( "Pop_up_edit" ) );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.TableItem[] items = table.getSelection();
                if (items.length == 0) {
                    return;
                }
                editEntry( items[0] );
            }
        } );
        item = new org.eclipse.swt.widgets.MenuItem( popUpMenu, SWT.PUSH );
        item.setText( resAddressBook.getString( "Pop_up_copy" ) );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.TableItem[] items = table.getSelection();
                if (items.length == 0) {
                    return;
                }
                copyBuffer = new java.lang.String[table.getColumnCount()];
                for (int i = 0; i < copyBuffer.length; i++) {
                    copyBuffer[i] = items[0].getText( i );
                }
            }
        } );
        item = new org.eclipse.swt.widgets.MenuItem( popUpMenu, SWT.PUSH );
        item.setText( resAddressBook.getString( "Pop_up_paste" ) );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                if (copyBuffer == null) {
                    return;
                }
                org.eclipse.swt.widgets.TableItem item = new org.eclipse.swt.widgets.TableItem( table, SWT.NONE );
                item.setText( copyBuffer );
                isModified = true;
            }
        } );
        item = new org.eclipse.swt.widgets.MenuItem( popUpMenu, SWT.PUSH );
        item.setText( resAddressBook.getString( "Pop_up_delete" ) );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.TableItem[] items = table.getSelection();
                if (items.length == 0) {
                    return;
                }
                items[0].dispose();
                isModified = true;
            }
        } );
        new org.eclipse.swt.widgets.MenuItem( popUpMenu, SWT.SEPARATOR );
        item = new org.eclipse.swt.widgets.MenuItem( popUpMenu, SWT.PUSH );
        item.setText( resAddressBook.getString( "Pop_up_find" ) );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                searchDialog.open();
            }
        } );
        return popUpMenu;
    }

    private void createHelpMenu( org.eclipse.swt.widgets.Menu menuBar )
    {
        org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem( menuBar, SWT.CASCADE );
        item.setText( resAddressBook.getString( "Help_menu_title" ) );
        org.eclipse.swt.widgets.Menu menu = new org.eclipse.swt.widgets.Menu( shell, SWT.DROP_DOWN );
        item.setMenu( menu );
        org.eclipse.swt.widgets.MenuItem subItem = new org.eclipse.swt.widgets.MenuItem( menu, SWT.NONE );
        subItem.setText( resAddressBook.getString( "About" ) );
        subItem.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                org.eclipse.swt.widgets.MessageBox box = new org.eclipse.swt.widgets.MessageBox( shell, SWT.NONE );
                box.setText( resAddressBook.getString( "About_1" ) + shell.getText() );
                box.setMessage( shell.getText() + resAddressBook.getString( "About_2" ) );
                box.open();
            }
        } );
    }

    private class RowComparator implements java.util.Comparator
    {

        private int column;

        public RowComparator( int col )
        {
            column = col;
        }

        public int compare( java.lang.Object obj1, java.lang.Object obj2 )
        {
            java.lang.String[] row1 = (java.lang.String[]) obj1;
            java.lang.String[] row2 = (java.lang.String[]) obj2;
            return row1[column].compareTo( row2[column] );
        }

    }

}
