package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import Hardware.HardDisk;
import Hardware.Memory;
import Root.Global;

public class Gui 
{
	private String imagePath;
	private int screenSolutionWidth;
	private int screenSolutionHeight;
	
	private JMenuBar jMenuBar = null;
		private JMenu fileMenu = null;
			private JMenuItem newFileMenuItem = null;
			private JMenuItem saveFileMenuItem = null;
			private JMenuItem saveFileAsMenuItem = null;
			private JMenuItem openFileMenuItem = null;
			private JMenuItem exitMenuItem = null;
			private JMenuItem minimizeMenuItem = null;
		private JMenu debugMenu = null;
			private JMenuItem runMenuItem = null;
			private JMenuItem singleStepMenuItem = null;
			private JMenuItem stepBackMenuItem = null;
			private JMenuItem reloadMenuItem = null;
	
	private JFrame jFrame = null;
		private JPanel jContentPane = null;
			private JSplitPane jSplitPanes = null;
				private JPanel headerBar = null;
					private JPanel programMenuBar = null;
						private JButton runButton = null;
						private JButton singleStepButton = null;
						private JButton stepBackButton = null;
						private JButton reloadButton = null;
					private JSplitPane jSplitMainPanes = null;
					private JSplitPane jSplitPaneTop = null;
						private JSplitPane jSplitPaneLeft = null;
							private JToolBar registers = null;
								private JPanel registerPanel = null;
								private JScrollPane registerVerticalScrollPane = null;
								private JScrollPane registerHorizontalScrollPane = null;
							private JSplitPane codeAreaSplitPane = null;
								private JPanel codePanel = null;
									private CodeArea codeArea = null;
									private JScrollPane codeVerticalScrollPane = null;
									private JScrollPane codeHorizontalScrollPane = null;
						private JPanel jPanelRight = null;
							private MemoryArea memoryArea = null;
							private JScrollPane memoryVerticalScrollPane = null;
							private JScrollPane memoryHorizontalScrollPane = null;
					private JToolBar consoleLog = null;
						private JPanel consolePanel = null;
							private ConsoleArea consoleLogTextArea = null;
							private JScrollPane consoleLogScrollPane = null;
	
	private GuiAction guiAction; 					
	public Gui() throws Exception
	{
		Memory memory = new Memory(Global.defaultMemorySize);
		Thread memoryThread = new Thread(memory);
		memoryThread.start();
		
		HardDisk disk = new HardDisk(Global.defaultDiskSize);
		guiAction = new GuiAction(memory, disk);
		this.getJFrame().setVisible(true);
	}

	private JFrame getJFrame() throws Exception 
	{
		if (jFrame == null) 
		{
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			screenSolutionWidth = gd.getDisplayMode().getWidth();
			screenSolutionHeight = gd.getDisplayMode().getHeight();
			
			jFrame = new JFrame();
			
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("Besembly");
			
			jFrame.setSize(screenSolutionWidth, screenSolutionHeight);
			
			jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			jFrame.setUndecorated(true);  
			
			jFrame.setJMenuBar(getJMenuBar());
			
			imagePath = new java.io.File("").getAbsolutePath();
			imagePath = imagePath + "//images//";
			ImageIcon img = new ImageIcon(imagePath+"icon.png");
			jFrame.setIconImage(img.getImage());
		}
		return jFrame;
	}
	private JPanel getJContentPane()
	{
		if (jContentPane == null) 
		{
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJSplitPanes(), BorderLayout.CENTER);
		}
		return jContentPane;
	}
	private JSplitPane getJSplitPanes()
	{
		if (jSplitPanes == null) 
		{
			jSplitPanes = new JSplitPane(JSplitPane.VERTICAL_SPLIT)
			{
				private static final long serialVersionUID = 1864649981622181328L;
				private final int location = screenSolutionWidth/32;
			    {
			        setDividerLocation(location);
			    }
			    @Override public int getDividerLocation(){return location;}
			    @Override public int getLastDividerLocation(){return location;}
			};
			jSplitPanes.setEnabled(false);
			jSplitPanes.setTopComponent(getHeaderBar());
			jSplitPanes.setBottomComponent(getJSplitMainPanes());
		}
		return jSplitPanes;
	}
	private JPanel getHeaderBar()
	{
		if (headerBar == null) 
		{
			headerBar = new JPanel();
			headerBar.setOpaque(false);
			headerBar.setEnabled(false);
			headerBar.setLayout(new BorderLayout());
			
			headerBar.add(getProgramMenuBar(), BorderLayout.CENTER);
			
			FlagsArea flags = FlagsArea.get();
			headerBar.add(flags, BorderLayout.EAST);
		}
		return headerBar;
	}
	private JPanel getProgramMenuBar()
	{
		if(programMenuBar == null)
		{
			programMenuBar = new JPanel();
			programMenuBar.setEnabled(false);
			
			programMenuBar.add(getRunButton());
			programMenuBar.add(getSingleStepButton());
			programMenuBar.add(getStepBackButton());     
			programMenuBar.add(getReloadButton());
		}
		return programMenuBar;   
	}
	private JButton getRunButton()
	{
		if (runButton == null) 
		{
			runButton = new JButton();
			runButton.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = -8240103838365318606L;

				public void actionPerformed(ActionEvent e)
            	{
					try {
						guiAction.Run();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
            	}
			});
			runButton.setText("Run");
		}
		return runButton;
	}
	private JButton getSingleStepButton()
	{
		if (singleStepButton == null) 
		{
			singleStepButton = new JButton();
			singleStepButton.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = -8240103838365318606L;

				public void actionPerformed(ActionEvent e)
            	{
					guiAction.SingleStep();
            	}
			});
			singleStepButton.setText("Single Step");
		}
		return singleStepButton;
	}
	private JButton getStepBackButton()
	{
		if (stepBackButton == null) 
		{
			stepBackButton = new JButton();
			stepBackButton.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = -8240103838365318606L;

				public void actionPerformed(ActionEvent e)
            	{
					guiAction.StepBack();
            	}
			});
			stepBackButton.setText("Step Back");
		}
		return stepBackButton;
	}
	private JButton getReloadButton()
	{
		if (reloadButton == null) 
		{
			reloadButton = new JButton();
			reloadButton.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = -8240103838365318606L;

				public void actionPerformed(ActionEvent e)
            	{
					guiAction.Reload();
            	}
			});
			reloadButton.setText("Reload");
		}
		return reloadButton;
	}
	
	private JSplitPane getJSplitMainPanes()
	{
		if (jSplitMainPanes == null) 
		{
			jSplitMainPanes = new JSplitPane(JSplitPane.VERTICAL_SPLIT)
			{
				private static final long serialVersionUID = 1864649981622181328L;
				private final int location = screenSolutionHeight - ((screenSolutionHeight*2)/5);
			    {
			        setDividerLocation(location);
			    }
			    @Override public int getDividerLocation(){return location;}
			    @Override public int getLastDividerLocation(){return location;}
			};
			jSplitMainPanes.setEnabled(false);
			jSplitMainPanes.setTopComponent(getJSplitPaneTop());
			jSplitMainPanes.setBottomComponent(getConsoleLog());
		}
		return jSplitMainPanes;
	}
	private JSplitPane getJSplitPaneTop() 
	{
		if (jSplitPaneTop == null) 
		{
			jSplitPaneTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
			{
				private static final long serialVersionUID = 1864649981622181328L;
				private final int location = screenSolutionWidth-screenSolutionWidth/12;
			    {
			        setDividerLocation(location);
			    }
			    @Override public int getDividerLocation(){return location;}
			    @Override public int getLastDividerLocation(){return location;}
			};
			jSplitPaneTop.setEnabled(false);
			jSplitPaneTop.setLeftComponent(getJSplitPaneLeft());
			jSplitPaneTop.setRightComponent(getJPanelRight());
		}
		return jSplitPaneTop;
	}
	private JPanel getJPanelRight() 
	{
		if (jPanelRight == null)
		{
			jPanelRight = new JPanel();
			jPanelRight.setLayout(new BorderLayout());
			jPanelRight.add(getMemoryArea(), BorderLayout.CENTER);
			jPanelRight.add(getMemoryVerticalScrollPane(), BorderLayout.CENTER);
			jPanelRight.add(getMemoryHorizontalScrollPane(), BorderLayout.CENTER);
		}
		return jPanelRight;
	}
	private JLabel getMemoryArea()
	{
		if (memoryArea == null) 
		{
			memoryArea = new MemoryArea(guiAction.getMemory());
			Thread memoryAreaThread = new Thread(memoryArea);
			memoryAreaThread.start();
		}
		return memoryArea;
	}
	private JScrollPane getMemoryVerticalScrollPane() 
	{
		if (memoryVerticalScrollPane == null) 
		{
			memoryVerticalScrollPane = new JScrollPane(getMemoryArea());
			memoryVerticalScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return memoryVerticalScrollPane;
	}
	private JScrollPane getMemoryHorizontalScrollPane() 
	{
		if (memoryHorizontalScrollPane == null) 
		{
			memoryHorizontalScrollPane = new JScrollPane(getMemoryArea());
			memoryHorizontalScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		}
		return memoryHorizontalScrollPane;
	}
	private JSplitPane getJSplitPaneLeft() 
	{
		if (jSplitPaneLeft == null)
		{
			jSplitPaneLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
			{
				private static final long serialVersionUID = 1864649981622181328L;
				private final int location = screenSolutionWidth/10;
			    {
			        setDividerLocation(location);
			    }
			    @Override public int getDividerLocation(){return location;}
			    @Override public int getLastDividerLocation(){return location;}
			};
			jSplitPaneLeft.setEnabled(false);
			jSplitPaneLeft.setLeftComponent(getRegisterPanel());
			jSplitPaneLeft.setRightComponent(getCodeAreaSplitPane());
		}
		return jSplitPaneLeft;
	}
	private JPanel getRegisterPanel()
	{
		if (registerPanel == null)
		{
			registerPanel = new JPanel();
			registerPanel.setLayout(new BorderLayout());
			registerPanel.add(getRegisters(), BorderLayout.CENTER);
			registerPanel.add(getRegisterVerticalScrollPane(), BorderLayout.CENTER);
			registerPanel.add(getRegisterHorizontalScrollPane(), BorderLayout.CENTER);
		}
		return registerPanel;
	}
	private JToolBar getRegisters()
	{
		if (registers == null)
		{
			registers = new JToolBar();
			registers.setOrientation(JToolBar.VERTICAL);
			registers.setEnabled(false);
	
			RegistersArea registersArea = RegistersArea.get();
			Thread registersAreaThread = new Thread(registersArea);
			registersAreaThread.start();
			
			registers.add(registersArea);
		}
		return registers;
	}
	private JScrollPane getRegisterVerticalScrollPane() 
	{
		if (registerVerticalScrollPane == null) 
		{
			registerVerticalScrollPane = new JScrollPane(getRegisters());
			registerVerticalScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return registerVerticalScrollPane;
	}
	private JScrollPane getRegisterHorizontalScrollPane() 
	{
		if (registerHorizontalScrollPane == null) 
		{
			registerHorizontalScrollPane = new JScrollPane(getRegisters());
			registerHorizontalScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		}
		return registerHorizontalScrollPane;
	}
	private JSplitPane getCodeAreaSplitPane() 
	{
		if (codeAreaSplitPane == null)
		{
			codeAreaSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
			{
				private static final long serialVersionUID = 1864649981622181328L;
				private final int location = screenSolutionWidth/96;
			    {
			        setDividerLocation(location);
			    }
			    @Override public int getDividerLocation(){return location;}
			    @Override public int getLastDividerLocation(){return location;}
			};
			codeAreaSplitPane.setEnabled(false);
			codeAreaSplitPane.setRightComponent(getCodePanel());
		}
		return codeAreaSplitPane;
	}
	private JPanel getCodePanel()
	{
		if (codePanel == null)
		{
			codePanel = new JPanel();
			codePanel.setLayout(new BorderLayout());
			codePanel.add(getCodeVerticalScrollPane(), BorderLayout.CENTER);
			codePanel.add(getCodeHorizontalScrollPane(), BorderLayout.CENTER);
			
			LineNumberPanel tln = new LineNumberPanel(codeArea);
			codePanel.add(tln, BorderLayout.WEST);
			
			JScrollPane scrollPane = new JScrollPane(codeArea);
			scrollPane.setRowHeaderView(tln);
			codePanel.add(scrollPane, BorderLayout.CENTER);
		}
		return codePanel;
	}
	
	private CodeArea getCodeArea()
	{
		if (codeArea == null) 
		{
			codeArea = CodeArea.get();
			codeArea.setEditable(true);
	        Font font = new Font("Verdana", Font.PLAIN, 18);
	        codeArea.setFont(font);
	        codeArea.setForeground(Color.BLACK);
		}
		return codeArea;
	}
	private JScrollPane getCodeVerticalScrollPane() 
	{
		if (codeVerticalScrollPane == null) 
		{
			codeVerticalScrollPane = new JScrollPane(getCodeArea());
			codeVerticalScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return codeVerticalScrollPane;
	}
	private JScrollPane getCodeHorizontalScrollPane() 
	{
		if (codeHorizontalScrollPane == null) 
		{
			codeHorizontalScrollPane = new JScrollPane(getCodeArea());
			codeHorizontalScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		}
		return codeHorizontalScrollPane;
	}
	
	private JToolBar getConsoleLog()
	{
		if (consoleLog == null) 
		{
			consoleLog = new JToolBar();
			consoleLog.setOrientation(JToolBar.VERTICAL);
			consoleLog.setEnabled(false);
			consoleLog.add(new JLabel("Console Log:"));
			consoleLog.add(getConsolePanel());
		}
		return consoleLog;
	}
	private JPanel getConsolePanel()
	{
		if (consolePanel == null)
		{
			consolePanel = new JPanel();
			consolePanel.setLayout(new BorderLayout());
			consolePanel.add(getConsoleLogTextArea(), BorderLayout.CENTER);
			consolePanel.add(getConsoleLogScrollPane(), BorderLayout.CENTER);
		}
		return consolePanel;
	}
	private JTextArea getConsoleLogTextArea()
	{
		if (consoleLogTextArea == null) 
		{
			consoleLogTextArea = ConsoleArea.get();
			consoleLogTextArea.setEditable(false);
	        Font font = new Font("Verdana", Font.BOLD, 12);
	        consoleLogTextArea.setFont(font);
	        consoleLogTextArea.setForeground(Color.BLACK);
		}
		return consoleLogTextArea;
	}
	private JScrollPane getConsoleLogScrollPane() 
	{
		if (consoleLogScrollPane == null) 
		{
			consoleLogScrollPane = new JScrollPane(getConsoleLogTextArea());
			consoleLogScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return consoleLogScrollPane;
	}

	private JMenuBar getJMenuBar() 
	{
		if (jMenuBar == null) 
		{
			jMenuBar = new JMenuBar();
			jMenuBar.add(getFileMenu());
			jMenuBar.add(getDebugMenu());
		}
		return jMenuBar;
	}
	private JMenu getFileMenu() 
	{
		if (fileMenu == null) 
		{
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getNewFile());
			fileMenu.add(getSaveFile());
			fileMenu.add(getSaveFileAs());
			fileMenu.add(getOpenFile());
			fileMenu.add(getMinimizeProgram());
			fileMenu.add(getExitProgram());
		}
		return fileMenu;
	}
	private JMenuItem getMinimizeProgram() 
	{	
		if (minimizeMenuItem == null) 
		{
			minimizeMenuItem = new JMenuItem();
			minimizeMenuItem.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 1557578154258547969L;

				public void actionPerformed(ActionEvent e)
            	{
					jFrame.setState(JFrame.ICONIFIED);
                }
            });
			minimizeMenuItem.setText("Minimize");
			minimizeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
		}
		return minimizeMenuItem;
	}
	private JMenuItem getExitProgram() 
	{	
		if (exitMenuItem == null) 
		{
			exitMenuItem = new JMenuItem();
			exitMenuItem.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 6711526971428867230L;

				public void actionPerformed(ActionEvent e)
            	{
					System.exit(0);
                }
            });
			exitMenuItem.setText("Exit");
			exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0));
		}
		return exitMenuItem;
	}
	private JMenuItem getNewFile() 
	{	
		if (newFileMenuItem == null) 
		{
			newFileMenuItem = new JMenuItem();
			newFileMenuItem.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 1557578154258547969L;

				public void actionPerformed(ActionEvent e)
            	{
					guiAction.NewFile();
                }
            });
			newFileMenuItem.setText("New File");
			newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		}
		return newFileMenuItem;
	}
	private JMenuItem getSaveFile() 
	{	
		if (saveFileMenuItem == null) 
		{
			saveFileMenuItem = new JMenuItem();
			saveFileMenuItem.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 1557578154258547969L;

				public void actionPerformed(ActionEvent e)
            	{
					guiAction.SaveFile();
                }
            });
			saveFileMenuItem.setText("Save File");
			saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		}
		return saveFileMenuItem;
	}
	private JMenuItem getSaveFileAs() 
	{	
		if (saveFileAsMenuItem == null) 
		{
			saveFileAsMenuItem = new JMenuItem();
			saveFileAsMenuItem.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 1557578154258547969L;

				public void actionPerformed(ActionEvent e)
            	{
					guiAction.SaveFileAs();
                }
            });
			saveFileAsMenuItem.setText("Save File As");
			saveFileAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		}
		return saveFileAsMenuItem;
	}
	private JMenuItem getOpenFile() 
	{	
		if (openFileMenuItem == null) 
		{
			openFileMenuItem = new JMenuItem();
			openFileMenuItem.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 1557578154258547969L;

				public void actionPerformed(ActionEvent e)
            	{
					guiAction.OpenFile();
                }
            });
			openFileMenuItem.setText("Open File");
			openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		}
		return openFileMenuItem;
	}
	private JMenu getDebugMenu() 
	{
		if (debugMenu == null) 
		{
			debugMenu = new JMenu();
			debugMenu.setText("Debug");
			debugMenu.add(getRun());
			debugMenu.add(getSingleStep());
			debugMenu.add(getStepBack());
			debugMenu.add(getReload());
		}
		return debugMenu;
	}
	private JMenuItem getRun() 
	{	
		if (runMenuItem == null) 
		{
			runMenuItem = new JMenuItem();
			runMenuItem.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 1557578154258547969L;

				public void actionPerformed(ActionEvent e)
            	{
					try {
						guiAction.Run();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                }
            });
			runMenuItem.setText("Run");
			runMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));
		}
		return runMenuItem;
	}
	private JMenuItem getSingleStep() 
	{	
		if (singleStepMenuItem == null) 
		{
			singleStepMenuItem = new JMenuItem();
			singleStepMenuItem.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 1557578154258547969L;

				public void actionPerformed(ActionEvent e)
            	{
					guiAction.SingleStep();
                }
            });
			singleStepMenuItem.setText("Single Step");
			singleStepMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6,0));
		}
		return singleStepMenuItem;
	}
	private JMenuItem getStepBack() 
	{	
		if (stepBackMenuItem == null) 
		{
			stepBackMenuItem = new JMenuItem();
			stepBackMenuItem.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 1557578154258547969L;

				public void actionPerformed(ActionEvent e)
            	{
					guiAction.StepBack();
                }
            });
			stepBackMenuItem.setText("Step Back");
			stepBackMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7,0));
		}
		return stepBackMenuItem;
	}
	private JMenuItem getReload() 
	{	
		if (reloadMenuItem == null) 
		{
			reloadMenuItem = new JMenuItem();
			reloadMenuItem.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 1557578154258547969L;

				public void actionPerformed(ActionEvent e)
            	{
					guiAction.Reload();
                }
            });
			reloadMenuItem.setText("Reload");
			reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8,0));
		}
		return reloadMenuItem;
	} 
}