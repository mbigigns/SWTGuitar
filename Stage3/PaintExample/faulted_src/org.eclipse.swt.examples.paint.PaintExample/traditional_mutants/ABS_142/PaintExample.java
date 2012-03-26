// This is mutant program.
// Author : ysma

package org.eclipse.swt.examples.paint;


import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.accessibility.*;
import java.io.*;
import java.text.*;
import java.util.*;


public class PaintExample
{

    private static java.util.ResourceBundle resourceBundle = ResourceBundle.getBundle( "examples_paint" );

    private org.eclipse.swt.widgets.Composite mainComposite;

    private org.eclipse.swt.widgets.Canvas activeForegroundColorCanvas;

    private org.eclipse.swt.widgets.Canvas activeBackgroundColorCanvas;

    private org.eclipse.swt.graphics.Color paintColorBlack;

    private org.eclipse.swt.graphics.Color paintColorWhite;

    private org.eclipse.swt.graphics.Color[] paintColors;

    private org.eclipse.swt.graphics.Font paintDefaultFont;

    private static final int numPaletteRows = 3;

    private static final int numPaletteCols = 50;

    private org.eclipse.swt.examples.paint.ToolSettings toolSettings;

    private org.eclipse.swt.examples.paint.PaintSurface paintSurface;

    static final int Pencil_tool = 0;

    static final int Airbrush_tool = 1;

    static final int Line_tool = 2;

    static final int PolyLine_tool = 3;

    static final int Rectangle_tool = 4;

    static final int RoundedRectangle_tool = 5;

    static final int Ellipse_tool = 6;

    static final int Text_tool = 7;

    static final int None_fill = 8;

    static final int Outline_fill = 9;

    static final int Solid_fill = 10;

    static final int Solid_linestyle = 11;

    static final int Dash_linestyle = 12;

    static final int Dot_linestyle = 13;

    static final int DashDot_linestyle = 14;

    static final int Font_options = 15;

    static final int Default_tool = Pencil_tool;

    static final int Default_fill = None_fill;

    static final int Default_linestyle = Solid_linestyle;

    public static final org.eclipse.swt.examples.paint.Tool[] tools = { new org.eclipse.swt.examples.paint.Tool( Pencil_tool, "Pencil", "tool", SWT.RADIO ), new org.eclipse.swt.examples.paint.Tool( Airbrush_tool, "Airbrush", "tool", SWT.RADIO ), new org.eclipse.swt.examples.paint.Tool( Line_tool, "Line", "tool", SWT.RADIO ), new org.eclipse.swt.examples.paint.Tool( PolyLine_tool, "PolyLine", "tool", SWT.RADIO ), new org.eclipse.swt.examples.paint.Tool( Rectangle_tool, "Rectangle", "tool", SWT.RADIO ), new org.eclipse.swt.examples.paint.Tool( RoundedRectangle_tool, "RoundedRectangle", "tool", SWT.RADIO ), new org.eclipse.swt.examples.paint.Tool( Ellipse_tool, "Ellipse", "tool", SWT.RADIO ), new org.eclipse.swt.examples.paint.Tool( Text_tool, "Text", "tool", SWT.RADIO ), new org.eclipse.swt.examples.paint.Tool( None_fill, "None", "fill", SWT.RADIO, new java.lang.Integer( ToolSettings.ftNone ) ), new org.eclipse.swt.examples.paint.Tool( Outline_fill, "Outline", "fill", SWT.RADIO, new java.lang.Integer( ToolSettings.ftOutline ) ), new org.eclipse.swt.examples.paint.Tool( Solid_fill, "Solid", "fill", SWT.RADIO, new java.lang.Integer( ToolSettings.ftSolid ) ), new org.eclipse.swt.examples.paint.Tool( Solid_linestyle, "Solid", "linestyle", SWT.RADIO, new java.lang.Integer( SWT.LINE_SOLID ) ), new org.eclipse.swt.examples.paint.Tool( Dash_linestyle, "Dash", "linestyle", SWT.RADIO, new java.lang.Integer( SWT.LINE_DASH ) ), new org.eclipse.swt.examples.paint.Tool( Dot_linestyle, "Dot", "linestyle", SWT.RADIO, new java.lang.Integer( SWT.LINE_DOT ) ), new org.eclipse.swt.examples.paint.Tool( DashDot_linestyle, "DashDot", "linestyle", SWT.RADIO, new java.lang.Integer( SWT.LINE_DASHDOT ) ), new org.eclipse.swt.examples.paint.Tool( Font_options, "Font", "options", SWT.PUSH ) };

    public PaintExample( org.eclipse.swt.widgets.Composite parent )
    {
        mainComposite = parent;
        initResources();
        initActions();
        init();
    }

    public static void main( java.lang.String[] args )
    {
        org.eclipse.swt.widgets.Display display = new org.eclipse.swt.widgets.Display();
        org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell( display );
        shell.setText( getResourceString( "window.title" ) );
        shell.setLayout( new org.eclipse.swt.layout.GridLayout() );
        org.eclipse.swt.examples.paint.PaintExample instance = new org.eclipse.swt.examples.paint.PaintExample( shell );
        instance.createToolBar( shell );
        org.eclipse.swt.widgets.Composite composite = new org.eclipse.swt.widgets.Composite( shell, SWT.NONE );
        composite.setLayout( new org.eclipse.swt.layout.FillLayout() );
        composite.setLayoutData( new org.eclipse.swt.layout.GridData( SWT.FILL, SWT.FILL, true, true ) );
        instance.createGUI( composite );
        instance.setDefaults();
        setShellSize( display, shell );
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        instance.dispose();
    }

    private void createToolBar( org.eclipse.swt.widgets.Composite parent )
    {
        org.eclipse.swt.widgets.ToolBar toolbar = new org.eclipse.swt.widgets.ToolBar( parent, SWT.NONE );
        java.lang.String group = null;
        for (int i = 0; i < tools.length; i++) {
            org.eclipse.swt.examples.paint.Tool tool = tools[i];
            if (group != null && !tool.group.equals( group )) {
                new org.eclipse.swt.widgets.ToolItem( toolbar, SWT.SEPARATOR );
            }
            group = tool.group;
            org.eclipse.swt.widgets.ToolItem item = addToolItem( toolbar, tool );
            if (i == Default_tool || i == Default_fill || i == Default_linestyle) {
                item.setSelection( true );
            }
        }
    }

    private org.eclipse.swt.widgets.ToolItem addToolItem( final org.eclipse.swt.widgets.ToolBar toolbar, final org.eclipse.swt.examples.paint.Tool tool )
    {
        final java.lang.String id = tool.group + '.' + tool.name;
        org.eclipse.swt.widgets.ToolItem item = new org.eclipse.swt.widgets.ToolItem( toolbar, tool.type );
        item.setText( getResourceString( id + ".label" ) );
        item.setToolTipText( getResourceString( id + ".tooltip" ) );
        item.setImage( tool.image );
        item.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                tool.action.run();
            }
        } );
        final int childID = toolbar.indexOf( item );
        toolbar.getAccessible().addAccessibleListener( new org.eclipse.swt.accessibility.AccessibleAdapter(){
            public void getName( org.eclipse.swt.accessibility.AccessibleEvent e )
            {
                if (e.childID == childID) {
                    e.result = getResourceString( id + ".description" );
                }
            }
        } );
        return item;
    }

    public void setDefaults()
    {
        setPaintTool( Default_tool );
        setFillType( Default_fill );
        setLineStyle( Default_linestyle );
        setForegroundColor( paintColorBlack );
        setBackgroundColor( paintColorWhite );
    }

    public void createGUI( org.eclipse.swt.widgets.Composite parent )
    {
        org.eclipse.swt.layout.GridLayout gridLayout;
        org.eclipse.swt.layout.GridData gridData;
        org.eclipse.swt.widgets.Composite displayArea = new org.eclipse.swt.widgets.Composite( parent, SWT.NONE );
        gridLayout = new org.eclipse.swt.layout.GridLayout();
        gridLayout.numColumns = 1;
        displayArea.setLayout( gridLayout );
        final org.eclipse.swt.widgets.Canvas paintCanvas = new org.eclipse.swt.widgets.Canvas( displayArea, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND );
        gridData = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL );
        paintCanvas.setLayoutData( gridData );
        paintCanvas.setBackground( paintColorWhite );
        final org.eclipse.swt.widgets.Composite colorFrame = new org.eclipse.swt.widgets.Composite( displayArea, SWT.NONE );
        gridData = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL );
        colorFrame.setLayoutData( gridData );
        final org.eclipse.swt.widgets.Composite toolSettingsFrame = new org.eclipse.swt.widgets.Composite( displayArea, SWT.NONE );
        gridData = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL );
        toolSettingsFrame.setLayoutData( gridData );
        final org.eclipse.swt.widgets.Text statusText = new org.eclipse.swt.widgets.Text( displayArea, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
        gridData = new org.eclipse.swt.layout.GridData( (-(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL)) );
        statusText.setLayoutData( gridData );
        paintSurface = new org.eclipse.swt.examples.paint.PaintSurface( paintCanvas, statusText, paintColorWhite );
        tools[Pencil_tool].data = new org.eclipse.swt.examples.paint.PencilTool( toolSettings, paintSurface );
        tools[Airbrush_tool].data = new org.eclipse.swt.examples.paint.AirbrushTool( toolSettings, paintSurface );
        tools[Line_tool].data = new org.eclipse.swt.examples.paint.LineTool( toolSettings, paintSurface );
        tools[PolyLine_tool].data = new org.eclipse.swt.examples.paint.PolyLineTool( toolSettings, paintSurface );
        tools[Rectangle_tool].data = new org.eclipse.swt.examples.paint.RectangleTool( toolSettings, paintSurface );
        tools[RoundedRectangle_tool].data = new org.eclipse.swt.examples.paint.RoundedRectangleTool( toolSettings, paintSurface );
        tools[Ellipse_tool].data = new org.eclipse.swt.examples.paint.EllipseTool( toolSettings, paintSurface );
        tools[Text_tool].data = new org.eclipse.swt.examples.paint.TextTool( toolSettings, paintSurface );
        gridLayout = new org.eclipse.swt.layout.GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        colorFrame.setLayout( gridLayout );
        activeForegroundColorCanvas = new org.eclipse.swt.widgets.Canvas( colorFrame, SWT.BORDER );
        gridData = new org.eclipse.swt.layout.GridData( GridData.HORIZONTAL_ALIGN_FILL );
        gridData.heightHint = 24;
        gridData.widthHint = 24;
        activeForegroundColorCanvas.setLayoutData( gridData );
        activeBackgroundColorCanvas = new org.eclipse.swt.widgets.Canvas( colorFrame, SWT.BORDER );
        gridData = new org.eclipse.swt.layout.GridData( GridData.HORIZONTAL_ALIGN_FILL );
        gridData.heightHint = 24;
        gridData.widthHint = 24;
        activeBackgroundColorCanvas.setLayoutData( gridData );
        final org.eclipse.swt.widgets.Canvas paletteCanvas = new org.eclipse.swt.widgets.Canvas( colorFrame, SWT.BORDER | SWT.NO_BACKGROUND );
        gridData = new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL );
        gridData.heightHint = 24;
        paletteCanvas.setLayoutData( gridData );
        paletteCanvas.addListener( SWT.MouseDown, new org.eclipse.swt.widgets.Listener(){
            public void handleEvent( org.eclipse.swt.widgets.Event e )
            {
                org.eclipse.swt.graphics.Rectangle bounds = paletteCanvas.getClientArea();
                org.eclipse.swt.graphics.Color color = getColorAt( bounds, e.x, e.y );
                if (e.button == 1) {
                    setForegroundColor( color );
                } else {
                    setBackgroundColor( color );
                }
            }

            private org.eclipse.swt.graphics.Color getColorAt( org.eclipse.swt.graphics.Rectangle bounds, int x, int y )
            {
                if (bounds.height <= 1 && bounds.width <= 1) {
                    return paintColorWhite;
                }
                final int row = (y - bounds.y) * numPaletteRows / bounds.height;
                final int col = (x - bounds.x) * numPaletteCols / bounds.width;
                return paintColors[Math.min( Math.max( row * numPaletteCols + col, 0 ), paintColors.length - 1 )];
            }
        } );
        org.eclipse.swt.widgets.Listener refreshListener = new org.eclipse.swt.widgets.Listener(){
            public void handleEvent( org.eclipse.swt.widgets.Event e )
            {
                if (e.gc == null) {
                    return;
                }
                org.eclipse.swt.graphics.Rectangle bounds = paletteCanvas.getClientArea();
                for (int row = 0; row < numPaletteRows; ++row) {
                    for (int col = 0; col < numPaletteCols; ++col) {
                        final int x = bounds.width * col / numPaletteCols;
                        final int y = bounds.height * row / numPaletteRows;
                        final int width = Math.max( bounds.width * (col + 1) / numPaletteCols - x, 1 );
                        final int height = Math.max( bounds.height * (row + 1) / numPaletteRows - y, 1 );
                        e.gc.setBackground( paintColors[row * numPaletteCols + col] );
                        e.gc.fillRectangle( bounds.x + x, bounds.y + y, width, height );
                    }
                }
            }
        };
        paletteCanvas.addListener( SWT.Resize, refreshListener );
        paletteCanvas.addListener( SWT.Paint, refreshListener );
        gridLayout = new org.eclipse.swt.layout.GridLayout();
        gridLayout.numColumns = 4;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        toolSettingsFrame.setLayout( gridLayout );
        org.eclipse.swt.widgets.Label label = new org.eclipse.swt.widgets.Label( toolSettingsFrame, SWT.NONE );
        label.setText( getResourceString( "settings.AirbrushRadius.text" ) );
        final org.eclipse.swt.widgets.Scale airbrushRadiusScale = new org.eclipse.swt.widgets.Scale( toolSettingsFrame, SWT.HORIZONTAL );
        airbrushRadiusScale.setMinimum( 5 );
        airbrushRadiusScale.setMaximum( 50 );
        airbrushRadiusScale.setSelection( toolSettings.airbrushRadius );
        airbrushRadiusScale.setLayoutData( new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );
        airbrushRadiusScale.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                toolSettings.airbrushRadius = airbrushRadiusScale.getSelection();
                updateToolSettings();
            }
        } );
        label = new org.eclipse.swt.widgets.Label( toolSettingsFrame, SWT.NONE );
        label.setText( getResourceString( "settings.AirbrushIntensity.text" ) );
        final org.eclipse.swt.widgets.Scale airbrushIntensityScale = new org.eclipse.swt.widgets.Scale( toolSettingsFrame, SWT.HORIZONTAL );
        airbrushIntensityScale.setMinimum( 1 );
        airbrushIntensityScale.setMaximum( 100 );
        airbrushIntensityScale.setSelection( toolSettings.airbrushIntensity );
        airbrushIntensityScale.setLayoutData( new org.eclipse.swt.layout.GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );
        airbrushIntensityScale.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
            {
                toolSettings.airbrushIntensity = airbrushIntensityScale.getSelection();
                updateToolSettings();
            }
        } );
    }

    public void dispose()
    {
        if (paintSurface != null) {
            paintSurface.dispose();
        }
        if (paintColors != null) {
            for (int i = 0; i < paintColors.length; ++i) {
                final org.eclipse.swt.graphics.Color color = paintColors[i];
                if (color != null) {
                    color.dispose();
                }
            }
        }
        paintDefaultFont = null;
        paintColors = null;
        paintSurface = null;
        freeResources();
    }

    public void freeResources()
    {
        for (int i = 0; i < tools.length; ++i) {
            org.eclipse.swt.examples.paint.Tool tool = tools[i];
            final org.eclipse.swt.graphics.Image image = tool.image;
            if (image != null) {
                image.dispose();
            }
            tool.image = null;
        }
    }

    public org.eclipse.swt.widgets.Display getDisplay()
    {
        return mainComposite.getDisplay();
    }

    public static java.lang.String getResourceString( java.lang.String key )
    {
        try {
            return resourceBundle.getString( key );
        } catch ( java.util.MissingResourceException e ) {
            return key;
        } catch ( java.lang.NullPointerException e ) {
            return "!" + key + "!";
        }
    }

    public static java.lang.String getResourceString( java.lang.String key, java.lang.Object[] args )
    {
        try {
            return MessageFormat.format( getResourceString( key ), args );
        } catch ( java.util.MissingResourceException e ) {
            return key;
        } catch ( java.lang.NullPointerException e ) {
            return "!" + key + "!";
        }
    }

    private void init()
    {
        org.eclipse.swt.widgets.Display display = mainComposite.getDisplay();
        paintColorWhite = new org.eclipse.swt.graphics.Color( display, 255, 255, 255 );
        paintColorBlack = new org.eclipse.swt.graphics.Color( display, 0, 0, 0 );
        paintDefaultFont = display.getSystemFont();
        paintColors = new org.eclipse.swt.graphics.Color[numPaletteCols * numPaletteRows];
        paintColors[0] = paintColorBlack;
        paintColors[1] = paintColorWhite;
        for (int i = 2; i < paintColors.length; i++) {
            paintColors[i] = new org.eclipse.swt.graphics.Color( display, i * 7 % 255, i * 23 % 255, i * 51 % 255 );
        }
        toolSettings = new org.eclipse.swt.examples.paint.ToolSettings();
        toolSettings.commonForegroundColor = paintColorBlack;
        toolSettings.commonBackgroundColor = paintColorWhite;
        toolSettings.commonFont = paintDefaultFont;
    }

    private void initActions()
    {
        for (int i = 0; i < tools.length; ++i) {
            final org.eclipse.swt.examples.paint.Tool tool = tools[i];
            java.lang.String group = tool.group;
            if (group.equals( "tool" )) {
                tool.action = new java.lang.Runnable(){
                    public void run()
                    {
                        setPaintTool( tool.id );
                    }
                };
            } else {
                if (group.equals( "fill" )) {
                    tool.action = new java.lang.Runnable(){
                        public void run()
                        {
                            setFillType( tool.id );
                        }
                    };
                } else {
                    if (group.equals( "linestyle" )) {
                        tool.action = new java.lang.Runnable(){
                            public void run()
                            {
                                setLineStyle( tool.id );
                            }
                        };
                    } else {
                        if (group.equals( "options" )) {
                            tool.action = new java.lang.Runnable(){
                                public void run()
                                {
                                    org.eclipse.swt.widgets.FontDialog fontDialog = new org.eclipse.swt.widgets.FontDialog( paintSurface.getShell(), SWT.PRIMARY_MODAL );
                                    org.eclipse.swt.graphics.FontData[] fontDatum = toolSettings.commonFont.getFontData();
                                    if (fontDatum != null && fontDatum.length > 0) {
                                        fontDialog.setFontList( fontDatum );
                                    }
                                    fontDialog.setText( getResourceString( "options.Font.dialog.title" ) );
                                    paintSurface.hideRubberband();
                                    org.eclipse.swt.graphics.FontData fontData = fontDialog.open();
                                    paintSurface.showRubberband();
                                    if (fontData != null) {
                                        try {
                                            org.eclipse.swt.graphics.Font font = new org.eclipse.swt.graphics.Font( mainComposite.getDisplay(), fontData );
                                            toolSettings.commonFont = font;
                                            updateToolSettings();
                                        } catch ( org.eclipse.swt.SWTException ex ) {
                                        }
                                    }
                                }
                            };
                        }
                    }
                }
            }
        }
    }

    public void initResources()
    {
        final java.lang.Class clazz = org.eclipse.swt.examples.paint.PaintExample.class;
        if (resourceBundle != null) {
            try {
                for (int i = 0; i < tools.length; ++i) {
                    org.eclipse.swt.examples.paint.Tool tool = tools[i];
                    java.lang.String id = tool.group + '.' + tool.name;
                    java.io.InputStream sourceStream = clazz.getResourceAsStream( getResourceString( id + ".image" ) );
                    org.eclipse.swt.graphics.ImageData source = new org.eclipse.swt.graphics.ImageData( sourceStream );
                    org.eclipse.swt.graphics.ImageData mask = source.getTransparencyMask();
                    tool.image = new org.eclipse.swt.graphics.Image( null, source, mask );
                    try {
                        sourceStream.close();
                    } catch ( java.io.IOException e ) {
                        e.printStackTrace();
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

    public void setFocus()
    {
        mainComposite.setFocus();
    }

    public void setForegroundColor( org.eclipse.swt.graphics.Color color )
    {
        if (activeForegroundColorCanvas != null) {
            activeForegroundColorCanvas.setBackground( color );
        }
        toolSettings.commonForegroundColor = color;
        updateToolSettings();
    }

    public void setBackgroundColor( org.eclipse.swt.graphics.Color color )
    {
        if (activeBackgroundColorCanvas != null) {
            activeBackgroundColorCanvas.setBackground( color );
        }
        toolSettings.commonBackgroundColor = color;
        updateToolSettings();
    }

    public void setPaintTool( int id )
    {
        org.eclipse.swt.examples.paint.PaintTool paintTool = (org.eclipse.swt.examples.paint.PaintTool) tools[id].data;
        paintSurface.setPaintSession( paintTool );
        updateToolSettings();
    }

    public void setFillType( int id )
    {
        java.lang.Integer fillType = (java.lang.Integer) tools[id].data;
        toolSettings.commonFillType = fillType.intValue();
        updateToolSettings();
    }

    public void setLineStyle( int id )
    {
        java.lang.Integer lineType = (java.lang.Integer) tools[id].data;
        toolSettings.commonLineStyle = lineType.intValue();
        updateToolSettings();
    }

    private static void setShellSize( org.eclipse.swt.widgets.Display display, org.eclipse.swt.widgets.Shell shell )
    {
        org.eclipse.swt.graphics.Rectangle bounds = display.getBounds();
        org.eclipse.swt.graphics.Point size = shell.computeSize( SWT.DEFAULT, SWT.DEFAULT );
        if (size.x > bounds.width) {
            size.x = bounds.width * 9 / 10;
        }
        if (size.y > bounds.height) {
            size.y = bounds.height * 9 / 10;
        }
        shell.setSize( size );
    }

    private void updateToolSettings()
    {
        final org.eclipse.swt.examples.paint.PaintTool activePaintTool = paintSurface.getPaintTool();
        if (activePaintTool == null) {
            return;
        }
        activePaintTool.endSession();
        activePaintTool.set( toolSettings );
        activePaintTool.beginSession();
    }

}
