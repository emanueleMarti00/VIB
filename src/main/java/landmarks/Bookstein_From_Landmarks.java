/* -*- mode: java; c-basic-offset: 8; indent-tabs-mode: t; tab-width: 8 -*- */

package landmarks;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import util.Overlay_Registered;

public class Bookstein_From_Landmarks extends BooksteinFromLandmarks
		implements PlugIn {
        public void run(String arg) {

                int[] wList = WindowManager.getIDList();
                if (wList==null) {
                        IJ.error("Bookstein_From_Landmarks.run(): No images are open");
                        return;
                }

                String[] titles = new String[wList.length+1];
                for (int i=0; i<wList.length; i++) {
                        ImagePlus imp = WindowManager.getImage(wList[i]);
                        titles[i] = imp!=null?imp.getTitle():"";
                }

                String none = "*None*";
                titles[wList.length] = none;

                GenericDialog gd = new GenericDialog("Thin Plate Spline Registration from Landmarks");
                gd.addChoice("Template stack:", titles, titles[0]);
                gd.addChoice("Stack to transform:", titles, titles[1]);

                gd.addCheckbox("Keep source images", true);
		gd.addCheckbox("Overlay result", true );

                gd.showDialog();
                if (gd.wasCanceled())
                        return;

                int[] index = new int[2];
                index[0] = gd.getNextChoiceIndex();
                index[1] = gd.getNextChoiceIndex();
                keepSourceImages = gd.getNextBoolean();
		boolean overlayResult = gd.getNextBoolean();

		setImages( WindowManager.getImage(wList[index[0]]), WindowManager.getImage(wList[index[1]]) );

		ImagePlus transformed = register();

		if( overlayResult ) {
			ImagePlus merged = Overlay_Registered.overlayToImagePlus( sourceImages[0], transformed );
			merged.setTitle( "Registered and Overlayed" );
			merged.show();
		} else
			transformed.show();
        }
}
