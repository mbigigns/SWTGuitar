package rs.ac.ns.ftn.tmd.fuzzy.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import rs.ac.ns.ftn.tmd.fuzzy.FuzzyFactory;
import rs.ac.ns.ftn.tmd.fuzzy.FuzzyNumber;
import rs.ac.ns.ftn.tmd.fuzzy.FuzzyFactory.InvalidStringRepresentationException;



public class FuzzyNumberDraw {

	
	
	private Display display = null;
	private Shell shell = null;
	private Text firstNumber = null;
	private Text secondNumber = null;
	private Composite canvas = null;
	private FuzzyNumber fn1, fn2, fres;
	private double SCALE = 10d;
	
	private Combo Ltype1, Rtype1;
	private Combo Ltype2, Rtype2;
	private Combo arithmetics;
	private ScrollBar scroll;
	private Shell aboutBox = null;
	
	private String exceptionMessage = null;
	
	public FuzzyNumberDraw() {
		this.initGUI();
	}
	
	
	private void initGUI() {
		// INIT SHELL
		this.display = new Display();
		Display.setAppName("Fuzzy number draw");

		this.shell = new Shell(display);
		this.shell.setLayout(new GridLayout());

		
		// ARITHMETICS CHOICE
		Group arithmeticsGroup = new Group(this.shell,SWT.SHADOW_ETCHED_IN);
		arithmeticsGroup.setLayout(new GridLayout(2, true));
		GridData d = new GridData(SWT.FILL,SWT.FILL,true,false);
		d.heightHint = 80;
		arithmeticsGroup.setLayoutData(d);
		arithmeticsGroup.setText("Arithmetics");
		this.arithmetics = new Combo(arithmeticsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.arithmetics.add("LR fuzzy numbers", 0);
		this.arithmetics.add("Decomposed fuzzy numbers", 1);
		this.arithmetics.select(0);
		GridData d2 = new GridData(SWT.FILL,SWT.CENTER,true,false);
		this.arithmetics.setLayoutData(d2);
		
		Composite functionComposite = new Composite(arithmeticsGroup, SWT.NONE);
		functionComposite.setLayout(new GridLayout(4,false));
		functionComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		new Label(functionComposite,SWT.LEFT).setText("Left function X");
		this.Ltype1 = new Combo(functionComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		new Label(functionComposite,SWT.LEFT).setText("Right function X");
		this.Rtype1 = new Combo(functionComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.Ltype1.add("Linear"); this.Rtype1.add("Linear");
		this.Ltype1.add("Gaussian"); this.Rtype1.add("Gaussian");
		this.Ltype1.add("Quadratic"); this.Rtype1.add("Quadratic");
		this.Ltype1.add("Exponential"); this.Rtype1.add("Exponential");
		this.Ltype1.select(0); this.Rtype1.select(0);
		
		new Label(functionComposite,SWT.LEFT).setText("Left function Y");
		this.Ltype2 = new Combo(functionComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		new Label(functionComposite,SWT.LEFT).setText("Right function Y");
		this.Rtype2 = new Combo(functionComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.Ltype2.add("Linear"); this.Rtype2.add("Linear");
		this.Ltype2.add("Gaussian"); this.Rtype2.add("Gaussian");
		this.Ltype2.add("Quadratic"); this.Rtype2.add("Quadratic");
		this.Ltype2.add("Exponential"); this.Rtype2.add("Exponential");
		this.Ltype2.select(0); this.Rtype2.select(0);
		
		
		// NUMBER INPUTS
		Group group = new Group(this.shell,SWT.SHADOW_ETCHED_IN);            
		group.setLayout(new GridLayout(8,false));
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.heightHint = 40;
		group.setLayoutData(gd);
		group.setText("Fuzzy numbers");
		new Label(group,SWT.LEFT).setText("X = [modal, alpha, beta]");
		this.firstNumber = getNumberInput(group);
		new Label(group,SWT.RIGHT).setText("          Y = [modal, alpha, beta]");
		this.secondNumber = getNumberInput(group);

		
		// CANVAS
		Group displayGroup = new Group(this.shell,SWT.SHADOW_ETCHED_IN);            
		displayGroup.setLayout(new GridLayout());
		displayGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		displayGroup.setText("Graphical");
		
		this.canvas = new Composite(displayGroup,SWT.BORDER | SWT.DOUBLE_BUFFERED | SWT.H_SCROLL);
		this.canvas.setBackground(this.display.getSystemColor(SWT.COLOR_WHITE));
		this.canvas.setLayout(new GridLayout());
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumHeight = 400;
		data.minimumWidth = 750;
		this.canvas.setLayoutData(data);
		
		this.canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				FuzzyNumberDraw.this.paintWidget(e.gc);
			}
		});
		
		
		// Zoom function (primitive)
		this.canvas.addMouseWheelListener(new MouseWheelListener() {
			
			public void mouseScrolled(MouseEvent arg0) {
				if (arg0.count>0) {
					FuzzyNumberDraw.this.SCALE /= 1.5d;
					FuzzyNumberDraw.this.scroll.setIncrement( (int)Math.round(FuzzyNumberDraw.this.scroll.getIncrement()/1.5d) );
				}
				else {
					FuzzyNumberDraw.this.SCALE *= 1.5d;
					FuzzyNumberDraw.this.scroll.setIncrement( (int)Math.round(FuzzyNumberDraw.this.scroll.getIncrement()*1.5d) );
				}
					
				FuzzyNumberDraw.this.canvas.redraw();
			}
		});
		
		// x coordinate info box
		this.canvas.addMouseMoveListener(new MouseMoveListener() {

			public void mouseMove(MouseEvent e) {
				FuzzyNumberDraw.this.canvas.setToolTipText("x="+ 
						(((double)e.x / (double)FuzzyNumberDraw.this.canvas.getSize().x)*2d*FuzzyNumberDraw.this.SCALE     
						-FuzzyNumberDraw.this.SCALE+((double)FuzzyNumberDraw.this.scroll.getSelection()-10000d)/100d)
				);
			}
			
		});
		
		
		// SCROLL BAR
		this.scroll = this.canvas.getHorizontalBar();
		this.scroll.setMinimum(0);
		this.scroll.setMaximum(20000);
		this.scroll.setSelection(10000);
		this.scroll.setIncrement(100);
		this.scroll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				FuzzyNumberDraw.this.canvas.redraw();
			}
		});
		
		// BUTTONS
		Composite buttonsComposite = new Composite (this.shell,SWT.NONE);
		buttonsComposite.setLayout(new GridLayout(6,true));
		buttonsComposite.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,false));
		this.makeOperationButton(buttonsComposite, "    X + Y    ", new SelectionAdapter() {
			            public void widgetSelected(SelectionEvent arg0) {
			            	FuzzyNumberDraw.this.instantiateFuzzyNumbers();
			            	if (FuzzyNumberDraw.this.fn1!=null && FuzzyNumberDraw.this.fn2!=null)
			            		try {
			            			FuzzyNumberDraw.this.fres = fn1.add(fn2);
			            			FuzzyNumberDraw.this.exceptionMessage = null;
			            		} catch (Exception ex) {
			            			FuzzyNumberDraw.this.exceptionMessage = ex.getMessage();
			            			FuzzyNumberDraw.this.fres=null;
			            		}
			            	else 
			            		fres=null;
			            }
					});
		this.makeOperationButton(buttonsComposite, "    X - Y    ", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	FuzzyNumberDraw.this.instantiateFuzzyNumbers();
            	if (FuzzyNumberDraw.this.fn1!=null && FuzzyNumberDraw.this.fn2!=null)
            		try {
            			FuzzyNumberDraw.this.fres = fn1.substract(fn2);
            			FuzzyNumberDraw.this.exceptionMessage = null;
            		} catch (Exception ex) {
            			FuzzyNumberDraw.this.exceptionMessage = ex.getMessage();
            			FuzzyNumberDraw.this.fres=null;
            		}

            	else 
            		fres=null;
            }
		});
		this.makeOperationButton(buttonsComposite, "    X * Y    ", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	FuzzyNumberDraw.this.instantiateFuzzyNumbers();
            	if (FuzzyNumberDraw.this.fn1!=null && FuzzyNumberDraw.this.fn2!=null)
            		try {
            			FuzzyNumberDraw.this.fres = fn1.multiply(fn2);
            			FuzzyNumberDraw.this.exceptionMessage = null;
            		} catch (Exception ex) {
            			FuzzyNumberDraw.this.exceptionMessage = ex.getMessage();
            			FuzzyNumberDraw.this.fres=null;
            		}

            	else 
            		fres=null;
            }
		});
		this.makeOperationButton(buttonsComposite, "    X / Y    ", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	FuzzyNumberDraw.this.instantiateFuzzyNumbers();
            	if (FuzzyNumberDraw.this.fn1!=null && FuzzyNumberDraw.this.fn2!=null)
            		try {
            			FuzzyNumberDraw.this.fres = fn1.divide(fn2);
            			FuzzyNumberDraw.this.exceptionMessage = null;
            		} catch (Exception ex) {
            			FuzzyNumberDraw.this.exceptionMessage = ex.getMessage();
            			FuzzyNumberDraw.this.fres=null;
            		}

            	else 
            		fres=null;
            }
		});

		this.makeOperationButton(buttonsComposite, "    1 / X    ", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
            	FuzzyNumberDraw.this.instantiateFuzzyNumbers();
            	if (FuzzyNumberDraw.this.fn1!=null)
            		try {
            			FuzzyNumberDraw.this.fres = fn1.getMultiplicationInverse();
            			FuzzyNumberDraw.this.exceptionMessage = null;
            		} catch (Exception ex) {
            			FuzzyNumberDraw.this.exceptionMessage = ex.getMessage();
            			FuzzyNumberDraw.this.fres=null;
            		}

            	else 
            		fres=null;
            }
		});
		// ABOUT BUTTON
		this.makeAboutButton(buttonsComposite);

        
		
		// KEEP OPEN :)
		this.shell.setMinimumSize(1024,768);
		this.shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		this.display.dispose();
	}
	
	
	
	
	/** PAINTER FUNCTION  */
	protected void paintWidget(GC graphics) {
		final int zeroAxis = (this.canvas.getSize().x)/2;
		final int firstAxis = (this.canvas.getSize().y)/6;
		final int secondAxis = 3*firstAxis;
		final int zAxis = 5*firstAxis;
		final int functionHeight = firstAxis;

		final int firstYOffset = -5;
		// TEXT
		graphics.setForeground(new Color(graphics.getDevice(),80,80,255));
		if (this.fn1 != null)
			graphics.drawText("X: "+this.fn1.toString(), 10, firstAxis-firstYOffset);
		else
			graphics.drawText("X: null", 10, firstAxis-firstYOffset);
		if (this.fn2 != null)
			graphics.drawText("Y: "+this.fn2.toString(), 10, secondAxis-firstYOffset);
		else
			graphics.drawText("Y: null", 10, secondAxis-firstYOffset);
		if (this.fres != null)
			graphics.drawText("Z: "+this.fres.toString(), 10, zAxis-firstYOffset);
		else
			graphics.drawText("Z: null", 10, zAxis-firstYOffset);
		
		
		// AXIS
		graphics.setForeground(new Color(graphics.getDevice(),190,180,180));
		graphics.drawLine(0, firstAxis, this.canvas.getSize().x, firstAxis);
		graphics.drawLine(0, secondAxis, this.canvas.getSize().x, secondAxis);
		graphics.drawLine(0, zAxis, this.canvas.getSize().x, zAxis);
		
		// ZERO AXIS
		
		graphics.drawLine(zeroAxis+(int)Math.round(((double)this.canvas.getSize().x/(this.SCALE*2))*(10000d-(double)this.scroll.getSelection())/100d),0,zeroAxis+(int)Math.round(((double)this.canvas.getSize().x/(double)(this.SCALE*2))*((10000d-(double)this.scroll.getSelection())/100d)),this.canvas.getSize().y);
		
		if (this.exceptionMessage!=null) {
			graphics.setForeground(new Color(graphics.getDevice(),255,20,20));
			graphics.drawText(this.exceptionMessage, 80, zAxis-80);
		}
		
		// FUZZY NUMBERS
		graphics.setForeground(new Color(graphics.getDevice(),100,80,100));
		if (this.fn1!=null) { 
				this.drawFNLR(this.fn1, graphics, zeroAxis, firstAxis, functionHeight);
		}
		if (this.fn2!=null) { 
				this.drawFNLR(this.fn2, graphics, zeroAxis, secondAxis, functionHeight);
		}
		if (this.fres!=null) { 
				this.drawFNLR(this.fres, graphics, zeroAxis, zAxis, functionHeight);
		}

		
		// dispose
		if(graphics != null && !graphics.isDisposed()){
			graphics.dispose();
		}
	}
	
	

	private void drawFNLR(FuzzyNumber fuzzy, GC graphics, int zeroAxis, int firstAxis, int functionHeight) {
		final double intervalSegmentation = 100d;
		double intLength = fuzzy.getLeftSpread()+fuzzy.getRightSpread();
		
		for (int i=0; i< intervalSegmentation; i++) {
			graphics.drawLine( zeroAxis +(int)Math.round((((double)this.canvas.getSize().x/(this.SCALE*2d))*(10000d-(double)this.scroll.getSelection() )/100d))
					+ (int)(Math.round(((fuzzy.getLeftBoundary()+intLength*i/intervalSegmentation)* (double)zeroAxis/this.SCALE))),
					firstAxis-(int)Math.round( functionHeight*fuzzy.getMembershipValue(fuzzy.getLeftBoundary()+intLength*i/intervalSegmentation)), 
					// lines
					zeroAxis +(int)Math.round((((double)this.canvas.getSize().x/(this.SCALE*2d))*(10000d-(double)this.scroll.getSelection() )/100d ))
					+ (int)(Math.round(((fuzzy.getLeftBoundary()+intLength*(i+1)/intervalSegmentation)* (double)zeroAxis/this.SCALE))),
					firstAxis-(int)Math.round( functionHeight*fuzzy.getMembershipValue(fuzzy.getLeftBoundary()+intLength*(i+1)/intervalSegmentation))
					// dots
		/*
					zeroAxis +(int)Math.round((((double)this.canvas.getSize().x/(double)(this.SCALE*2))*(100d-(double)this.scroll.getSelection())))
					+ (int)(Math.round(((fuzzy.getLeftBoundary()+intLength*i/intervalSegmentation)* (double)zeroAxis/(double)this.SCALE))),
					firstAxis-(int)Math.round( functionHeight*fuzzy.getMembershipValue(fuzzy.getLeftBoundary()+intLength*i/intervalSegmentation))
		*/
			);
			
		}
		
	}

/*
	private void drawFNDecomposed(DecomposedFuzzyNumber fn1, GC graphics, int zeroAxis, int firstAxis, int functionHeight) {
		
		double[] left = fn1.getLeftCuts();
		double[] right = fn1.getRightCuts();
		for (int i=0; i<left.length-1; i++) {
			graphics.drawLine( zeroAxis +(int)Math.round(((double)this.canvas.getSize().x/(double)(this.SCALE*2))*(100d-this.scroll.getSelection()))    + (int)(Math.round(left[i]* zeroAxis/this.SCALE)),
					firstAxis-(int)Math.round( functionHeight* i/left.length), 
					zeroAxis +(int)Math.round((((double)this.canvas.getSize().x/(double)(this.SCALE*2))*(100d-(double)this.scroll.getSelection())))    + (int)(Math.round(left[i+1]* zeroAxis/this.SCALE)),
					firstAxis-(int)Math.round( functionHeight * (i+1)/left.length ) 
			);
			graphics.drawLine( zeroAxis + (int)Math.round(((double)this.canvas.getSize().x/(double)(this.SCALE*2))*(100d-this.scroll.getSelection())) 
					+ (int)(Math.round(right[i]* zeroAxis/this.SCALE)),
					firstAxis-(int)Math.round(functionHeight*i/left.length), 
					zeroAxis +(int)Math.round((((double)this.canvas.getSize().x/(double)(this.SCALE*2))*(100d-this.scroll.getSelection())))
					+ (int)(Math.round(right[i+1]* zeroAxis/this.SCALE)),
					firstAxis-(int)Math.round( functionHeight * (i+1)/left.length ) 
			);
			
			
		}
		
	}
*/
	

	private Text getNumberInput(Group group) {
		Text temp = new Text(group, SWT.SINGLE );
		temp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,false,2,1));
		((GridData)temp.getLayoutData()).heightHint=15;
		((GridData)temp.getLayoutData()).widthHint=100;
		return temp;
	}
	
	

	private void makeOperationButton(Composite composite, String op, SelectionListener listener) {
		Button calculateB = new Button(composite, SWT.PUSH);
		calculateB.setText(op);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 100;
		data.minimumHeight = 40;
        calculateB.setLayoutData(data);
        calculateB.addSelectionListener(listener);

	}
	

	private void makeAboutButton(Composite composite) {
		Button calculateB = new Button(composite, SWT.PUSH);
		calculateB.setText("About FuzzyArithmetics");
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 100;
		data.minimumHeight = 25;
        calculateB.setLayoutData(data);
        calculateB.setBackground(new Color(this.display,210,210,255));
        calculateB.addSelectionListener(
        		new SelectionAdapter() {
		            public void widgetSelected(SelectionEvent arg0) {
		            	aboutBox = new Shell(FuzzyNumberDraw.this.display);
		            	aboutBox.setLocation(shell.getLocation().x+325, shell.getLocation().y+300);
		            	aboutBox.setBackground(new Color(display,255,255,255));
		            	aboutBox.setSize(390, 230);
		            	aboutBox.setLayout(new GridLayout());
		            	aboutBox.setText("About FuzzyNumeberDraw");
		        		aboutBox.setLayout(new GridLayout(1, true));
		        		aboutBox.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		        		Label app_name = new Label(aboutBox,0);
		        		app_name.setText("\n                                 FuzzyNumberDraw\n");
		        		app_name.setBackground(new Color(display,255,255,255));
		        		Label about = new Label(aboutBox,0);
		        		about.setText("     Demonstrates the use of FuzzyArithmetics library.\n\n     Author: Nikola Kolarovic <nikola.kolarovic@gmail.com>\n     http://sourceforge.net/projects/fuzzyarith/\n\n");
		        		about.setBackground(new Color(display,255,255,255));
		        		
		        		GridData data2 = new GridData(SWT.CENTER, SWT.NONE, true, true);
		        		Button bOK = new Button (aboutBox, SWT.PUSH);
		        		data2.minimumWidth = 100;
		        		data2.minimumHeight = 25;
		        		bOK.setLayoutData(data2);
		        		bOK.setText("  OK  ");
		        		bOK.setBackground(new Color(display,210,210,255));
		        		bOK.addSelectionListener(new SelectionAdapter() {
		        			public void widgetSelected(SelectionEvent arg0) {
		        				FuzzyNumberDraw.this.aboutBox.close();
		        			}
		        		});
		            	
		            	aboutBox.open();
		            	
	            }
        });
	}
	
	
	/** instantiates fuzzy number operands from textboxes in dialog */
	protected void instantiateFuzzyNumbers() {
    	// TODO: modify the factory
    	if (this.arithmetics.getSelectionIndex()==0) { // LR
	    	try {
	    		this.fn1 = FuzzyFactory.createArbitraryLRFuzzyNumber(FuzzyNumberDraw.this.firstNumber.getText(),this.Ltype1.getSelectionIndex(),this.Rtype1.getSelectionIndex());
	    	} catch (InvalidStringRepresentationException ex) {
	    		this.fn1 = null;
	    	}
	   		try {
				this.fn2 = FuzzyFactory.createArbitraryLRFuzzyNumber(FuzzyNumberDraw.this.secondNumber.getText(),this.Ltype2.getSelectionIndex(),this.Rtype2.getSelectionIndex());
			} catch (InvalidStringRepresentationException e) {
	    		this.fn2 = null;
			}
    	}
    	else { 
	    	try {
	    		this.fn1 = FuzzyFactory.createArbitraryDecomposedFuzzyNumber(FuzzyNumberDraw.this.firstNumber.getText(),this.Ltype1.getSelectionIndex(),this.Rtype1.getSelectionIndex());
	    	} catch (InvalidStringRepresentationException ex) {
	    		this.fn1 = null;
	    	}
	   		try {
				this.fn2 = FuzzyFactory.createArbitraryDecomposedFuzzyNumber(FuzzyNumberDraw.this.secondNumber.getText(),this.Ltype2.getSelectionIndex(),this.Rtype2.getSelectionIndex());
			} catch (InvalidStringRepresentationException e) {
	    		this.fn2 = null;
			}
    		
    	}
		
		this.canvas.redraw();
	}
	
	
	
	public static void main(String args[]) {
		
		new FuzzyNumberDraw();
		 
	}
	
}