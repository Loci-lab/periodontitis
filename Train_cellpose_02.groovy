import qupath.ext.biop.cellpose.Cellpose2D

// Specify the model name (cyto, nuc, cyto2, omni_bact or a path to your custom model)
def pathModel = '/gpfs/gsfs11/users/perezriverosp/hiplex_oct_nov_2022/QUAPATH04_TrainCellpose_02/models/cellpose_residual_on_style_on_concatenation_off_train_2023_02_02_15_47_26.273513'
def cellpose = Cellpose2D.builder( "pathModel" )
        .pixelSize( 0.5 )             // Resolution for detection in um
        .channels( "Average channels", "DAPI" )	      // Select detection channel(s)
//        .preprocess( ImageOps.Filters.median(1) )                // List of preprocessing ImageOps to run on the images before exporting them
        .normalizePercentilesGlobal(0.1, 99.8, 10) // Convenience global percentile normalization. arguments are percentileMin, percentileMax, dowsample.
        .epochs(500)             // Optional: will default to 500
        .tileSize(512)                  // If your GPU can take it, make larger tiles to process fewer of them. Useful for Omnipose
        .cellposeChannels(1,2)         // Overwrites the logic of this plugin with these two values. These will be sent directly to --chan and --chan2
//        .cellprobThreshold(0.0)        // Threshold for the mask detection, defaults to 0.0
//        .flowThreshold(0.4)            // Threshold for the flows, defaults to 0.4 
//        .diameter(15)                    // Median object diameter. Set to 0.0 for the `bact_omni` model or for automatic computation
          .addParameter("save_flows")      // Any parameter from cellpose not available in the builder. See https://cellpose.readthedocs.io/en/latest/command.html
//        .addParameter("anisotropy", "3") // Any parameter from cellpose not available in the builder. See https://cellpose.readthedocs.io/en/latest/command.html
//        .cellExpansion(5.0)              // Approximate cells based upon nucleus expansion
//        .cellConstrainScale(1.5)       // Constrain cell expansion using nucleus size
        .classify("My Detections")     // PathClass to give newly created objects
//        .measureShape()                // Add shape measurements
//        .measureIntensity()             // Add cell measurements (in all compartments)  
//        .createAnnotations()           // Make annotations instead of detections. This ignores cellExpansion
//        .simplify(0)                   // Simplification 1.6 by default, set to 0 to get the cellpose masks as precisely as possible
        .build()

// Once ready for training you can call the train() method
// train() will:
// 1. Go through the current project and save all "Training" and "Validation" regions into a temp folder (inside the current project)
// 2. Run the cellpose training via command line
// 3. Recover the model file after training, and copy it to where you defined in the builder, returning the reference to it
// 4. If it detects the run-cellpose-qc.py file in your QuPath Extensions Folder, it will run validation for this model

def resultModel = cellpose.train()

// Pick up results to see how the training was performed
println "Model Saved under "
println resultModel.getAbsolutePath().toString()

// You can get a ResultsTable of the training. 
def results = cellpose.getTrainingResults()
results.show("Training Results")

// You can get a results table with the QC results to visualize 
def qcResults = cellpose.getQCResults()
qcResults.show("QC Results")


// Finally you have access to a very simple graph 
cellpose.showTrainingGraph()