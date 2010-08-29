/*
 * JPinaplView.java
 */
package jpinapl;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import jpinapl.core.IStepNotifier;
import jpinapl.core.PPoint;
import jpinapl.core.PointDirection;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import jpinapl.core.Program;

/**
 * The application's main frame.
 */
public class JPinaplView extends FrameView {

    Program program;
    ImagePanel imagePanel;
    MouseMotionListener imageDragger;
    double xdragStart;
    double ydragStart;
    double xposStart;
    double yposStart;

    public JPinaplView(SingleFrameApplication app) {
        super(app);

        initComponents();
        JFileChooser jc = new JFileChooser();
        jc.setDialogTitle("Open a PINAPL image to interpret");
        jc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                for (String s : ImageIO.getReaderFileSuffixes()) {
                    if (f.getAbsolutePath().endsWith(s)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Supported image formats";
            }
        });
        int ret = jc.showOpenDialog(jPanel1);
        if (ret == jc.APPROVE_OPTION) {
            File file_to_open = jc.getSelectedFile();


            program = new Program(file_to_open);

            imagePanel = new ImagePanel();
            imagePanel.setImage(program.code.dispimg.image);
            jPanel1.add(imagePanel);
            imagePanel.setVisible(true);
            imagePanel.setProgram(program);

            program.addStepNotifier(imagePanel.stepNotifier);
            program.addStepNotifier(new IStepNotifier() {

                public void prod() {
                    updatestats_and_output();
                }
            });



            imageDragger = new MouseMotionListener() {

                public void mouseDragged(MouseEvent e) {
                    imagePanel.location.x = (int) ((e.getXOnScreen() - xdragStart) + xposStart);
                    imagePanel.location.y = (int) ((e.getYOnScreen() - ydragStart) + yposStart);
                    imagePanel.recomputeLocation();
                    //System.err.println("DRAGGED: " + e.getX() + " - " + e.getY());
                }

                public void mouseMoved(MouseEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet.");
                }
            };

            imagePanel.addMouseListener(new MouseListener() {

                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == e.BUTTON3) { //right click; add breakpoint
                        //System.err.println(e.getX() / imagePanel.zoom + " - " + e.getY() / imagePanel.zoom);
                        program.code.breakpoints.add(new PPoint((int) (e.getX() / imagePanel.zoom), (int) (e.getY() / imagePanel.zoom)));
                        imagePanel.stepNotifier.prod();
                    }
                }

                public void mousePressed(MouseEvent e) {
                    if (e.getButton() != e.BUTTON3) {
                        xdragStart = e.getXOnScreen();
                        ydragStart = e.getYOnScreen();
                        xposStart = imagePanel.location.x;
                        yposStart = imagePanel.location.y;
                        imagePanel.addMouseMotionListener(imageDragger);
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    imagePanel.removeMouseMotionListener(imageDragger);
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }
            });
        } else {
            System.exit(0);
        }
    }

    //Update the listbox with items! woo-hoo!
    private void updatestats_and_output() {
        if (program.code.stdout != null) {
            if (!program.code.stdout.equals("")) {
                TAOutput.append(program.code.stdout);
                if (Cautoscroll.isSelected()) {
                    TAOutput.setCaretPosition(TAOutput.getText().length());
                }
                program.code.stdout = "";
            }
        }

        TPStats.setText(program.prgStats);

        String tempstring = "";
        //listBox1.Items.Add("Executepixel = " + (code.executepixel? "true" : "False"));
        tempstring = "ProgramStack:[";
        for (PointDirection pd : program.code.programstack) {
            tempstring += " ";
            tempstring += pd.toString();
        }
        tempstring += "]\n";

        tempstring += "Stack:[";
        for (int i : program.code.stack) {
            tempstring += " " + i;
        }
        tempstring += "]\n";
        for (int i = 0; i < 256; i++) {
            tempstring += "i[" + i + "] = " + program.code.integers[i] + "\n";
        }
        TPVars.setText(tempstring);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TPStats = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        TPVars = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        ButtonStartStop = new javax.swing.JButton();
        ButtonStep = new javax.swing.JButton();
        ButtonRunBrkpt = new javax.swing.JButton();
        Cautoscroll = new javax.swing.JCheckBox();
        Cnopspeedup = new javax.swing.JCheckBox();
        Conlymasknops = new javax.swing.JCheckBox();
        Cshowlocbps = new javax.swing.JCheckBox();
        ButtonClearMasks = new javax.swing.JButton();
        SliderTimerInterval = new javax.swing.JSlider();
        LabelTimerInterval = new javax.swing.JLabel();
        SliderZoom = new javax.swing.JSlider();
        LabelZoom = new javax.swing.JLabel();
        SliderStepsPerTick = new javax.swing.JSlider();
        LabelStepsPerTick = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        TAOutput = new javax.swing.JTextArea();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(jpinapl.JPinaplApp.class).getContext().getResourceMap(JPinaplView.class);
        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setName("jPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 768, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 331, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        TPStats.setColumns(20);
        TPStats.setEditable(false);
        TPStats.setRows(5);
        TPStats.setName("TPStats"); // NOI18N
        jScrollPane1.setViewportView(TPStats);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        TPVars.setColumns(20);
        TPVars.setEditable(false);
        TPVars.setRows(5);
        TPVars.setName("TPVars"); // NOI18N
        jScrollPane2.setViewportView(TPVars);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setName("jPanel3"); // NOI18N

        ButtonStartStop.setText(resourceMap.getString("ButtonStartStop.text")); // NOI18N
        ButtonStartStop.setName("ButtonStartStop"); // NOI18N
        ButtonStartStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonStartStopActionPerformed(evt);
            }
        });

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(jpinapl.JPinaplApp.class).getContext().getActionMap(JPinaplView.class, this);
        ButtonStep.setAction(actionMap.get("step")); // NOI18N
        ButtonStep.setText(resourceMap.getString("ButtonStep.text")); // NOI18N
        ButtonStep.setName("ButtonStep"); // NOI18N
        ButtonStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonStepActionPerformed(evt);
            }
        });

        ButtonRunBrkpt.setText(resourceMap.getString("ButtonRunBrkpt.text")); // NOI18N
        ButtonRunBrkpt.setName("ButtonRunBrkpt"); // NOI18N

        Cautoscroll.setSelected(true);
        Cautoscroll.setText(resourceMap.getString("Cautoscroll.text")); // NOI18N
        Cautoscroll.setName("Cautoscroll"); // NOI18N

        Cnopspeedup.setText(resourceMap.getString("Cnopspeedup.text")); // NOI18N
        Cnopspeedup.setName("Cnopspeedup"); // NOI18N
        Cnopspeedup.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CnopspeedupStateChanged(evt);
            }
        });

        Conlymasknops.setText(resourceMap.getString("Conlymasknops.text")); // NOI18N
        Conlymasknops.setName("Conlymasknops"); // NOI18N
        Conlymasknops.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ConlymasknopsStateChanged(evt);
            }
        });

        Cshowlocbps.setSelected(true);
        Cshowlocbps.setText(resourceMap.getString("Cshowlocbps.text")); // NOI18N
        Cshowlocbps.setName("Cshowlocbps"); // NOI18N
        Cshowlocbps.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CshowlocbpsStateChanged(evt);
            }
        });

        ButtonClearMasks.setText(resourceMap.getString("ButtonClearMasks.text")); // NOI18N
        ButtonClearMasks.setName("ButtonClearMasks"); // NOI18N

        SliderTimerInterval.setMaximum(1000);
        SliderTimerInterval.setMinorTickSpacing(100);
        SliderTimerInterval.setPaintTicks(true);
        SliderTimerInterval.setValue(1);
        SliderTimerInterval.setName("SliderTimerInterval"); // NOI18N
        SliderTimerInterval.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SliderTimerIntervalStateChanged(evt);
            }
        });

        LabelTimerInterval.setText(resourceMap.getString("LabelTimerInterval.text")); // NOI18N
        LabelTimerInterval.setName("LabelTimerInterval"); // NOI18N

        SliderZoom.setMajorTickSpacing(10);
        SliderZoom.setMaximum(50);
        SliderZoom.setMinimum(5);
        SliderZoom.setMinorTickSpacing(5);
        SliderZoom.setPaintTicks(true);
        SliderZoom.setValue(20);
        SliderZoom.setName("SliderZoom"); // NOI18N
        SliderZoom.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SliderZoomStateChanged(evt);
            }
        });

        LabelZoom.setText(resourceMap.getString("LabelZoom.text")); // NOI18N
        LabelZoom.setName("LabelZoom"); // NOI18N

        SliderStepsPerTick.setMaximum(1000);
        SliderStepsPerTick.setMinimum(1);
        SliderStepsPerTick.setMinorTickSpacing(100);
        SliderStepsPerTick.setPaintTicks(true);
        SliderStepsPerTick.setValue(1);
        SliderStepsPerTick.setName("SliderStepsPerTick"); // NOI18N
        SliderStepsPerTick.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SliderStepsPerTickStateChanged(evt);
            }
        });

        LabelStepsPerTick.setText(resourceMap.getString("LabelStepsPerTick.text")); // NOI18N
        LabelStepsPerTick.setName("LabelStepsPerTick"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ButtonRunBrkpt, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ButtonClearMasks, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ButtonStep, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ButtonStartStop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Cautoscroll)
                                    .addComponent(Cnopspeedup)
                                    .addComponent(Conlymasknops))
                                .addGap(119, 119, 119)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SliderZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(LabelZoom)))
                            .addComponent(Cshowlocbps, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SliderStepsPerTick, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LabelStepsPerTick)
                            .addComponent(SliderTimerInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LabelTimerInterval))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(Cautoscroll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cnopspeedup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Conlymasknops)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cshowlocbps))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ButtonStartStop, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ButtonStep)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ButtonRunBrkpt, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ButtonClearMasks, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SliderZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(LabelTimerInterval)
                                    .addComponent(LabelZoom))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(SliderTimerInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addComponent(LabelStepsPerTick)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SliderStepsPerTick, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        TAOutput.setColumns(20);
        TAOutput.setEditable(false);
        TAOutput.setRows(5);
        TAOutput.setName("TAOutput"); // NOI18N
        jScrollPane4.setViewportView(TAOutput);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setComponent(mainPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void SliderZoomStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SliderZoomStateChanged
        imagePanel.zoom = SliderZoom.getValue() * 0.1;
        LabelZoom.setText("Zoom: " + imagePanel.zoom + "x");
        imagePanel.recomputeSize();
    }//GEN-LAST:event_SliderZoomStateChanged

    private void SliderTimerIntervalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SliderTimerIntervalStateChanged
        program.setTimerInterval(SliderTimerInterval.getValue());
        LabelTimerInterval.setText("Timer interval: " + program.timerInterval);
    }//GEN-LAST:event_SliderTimerIntervalStateChanged

    private void SliderStepsPerTickStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SliderStepsPerTickStateChanged
        program.stepsPerTick = SliderStepsPerTick.getValue();
        LabelStepsPerTick.setText("Number of steps per tick: " + program.stepsPerTick);
    }

    @Action
    public void stepBTN() {
    }//GEN-LAST:event_SliderStepsPerTickStateChanged

    private void ButtonStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonStepActionPerformed
        program.step(true);
    }//GEN-LAST:event_ButtonStepActionPerformed

    private void ButtonStartStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonStartStopActionPerformed
        program.startstop();
    }//GEN-LAST:event_ButtonStartStopActionPerformed

    private void CnopspeedupStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_CnopspeedupStateChanged
        program.code.speedupwhite = Cnopspeedup.isSelected();
    }//GEN-LAST:event_CnopspeedupStateChanged

    private void ConlymasknopsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ConlymasknopsStateChanged
        program.code.maskonlyNOPs = Conlymasknops.isSelected();
    }//GEN-LAST:event_ConlymasknopsStateChanged

    private void CshowlocbpsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_CshowlocbpsStateChanged
        imagePanel.showPos_N_BPs = Cshowlocbps.isSelected();
        imagePanel.stepNotifier.prod();
    }//GEN-LAST:event_CshowlocbpsStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonClearMasks;
    private javax.swing.JButton ButtonRunBrkpt;
    private javax.swing.JButton ButtonStartStop;
    private javax.swing.JButton ButtonStep;
    private javax.swing.JCheckBox Cautoscroll;
    private javax.swing.JCheckBox Cnopspeedup;
    private javax.swing.JCheckBox Conlymasknops;
    private javax.swing.JCheckBox Cshowlocbps;
    private javax.swing.JLabel LabelStepsPerTick;
    private javax.swing.JLabel LabelTimerInterval;
    private javax.swing.JLabel LabelZoom;
    private javax.swing.JSlider SliderStepsPerTick;
    private javax.swing.JSlider SliderTimerInterval;
    private javax.swing.JSlider SliderZoom;
    private javax.swing.JTextArea TAOutput;
    private javax.swing.JTextArea TPStats;
    private javax.swing.JTextArea TPVars;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
