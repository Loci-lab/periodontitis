import qupath.ext.biop.cellpose.Cellpose2D
import qupath.lib.images.ImageData
import qupath.lib.images.servers.ConcatChannelsImageServer
import qupath.lib.images.servers.TransformedServerBuilder

//First set channel name DAPI
setChannelNames('DAPI')

// Some server magic. Extract channels of interest and project them.
def avgServer = new TransformedServerBuilder( getCurrentServer() ).extractChannels(2,3,4,7,8,9,13,14,16,17,18).averageChannelProject().build()

// Extract the one other channel we want
def singleChannel = new TransformedServerBuilder( getCurrentServer() ).extractChannels('DAPI').build()

// Make a combined server. Notice the order here is DAPI first, then the average
def combined = new ConcatChannelsImageServer( getCurrentServer(), [singleChannel, avgServer] )

// Need to create a new in-place ImageData for cellpose later
def imageData = new ImageData(combined)

// run Cellpose. Careful of the channels names
def pathModel = 'tn'
def cellpose = Cellpose2D.builder( pathModel )
        .pixelSize( 0.5 )             // Resolution for detection in um
        .channels( "Average channels", "DAPI" )	      // Select detection channel(s)
        .normalizePercentilesGlobal(0.1, 99.8, 10)
        .tileSize(512)                  // If your GPU can take it, make larger tiles to process fewer of them. 
        .cellposeChannels(1,2)         // Need these, otherwise it just sends the one channel. These will be sent directly to --chan and --chan2
        .diameter(0)                    // Median object diameter. Set to 0.0 for the `bact_omni` model or for automatic computation
//         .cellExpansion(5.0)              // Approximate cells based upon nucleus expansion
        .measureShape()                // Add shape measurements
        .measureIntensity()             // Add cell measurements (in all compartments)  
        .simplify(0)                   // Simplification 1.6 by default, set to 0 to get the cellpose masks as precisely as possible
        .build()

// Run detection for the selected objects
def annotations = getSelectedObjects() 

// Note here that it is the imageData we created above and not the result of getCurrentImageData()
cellpose.detectObjects(imageData, annotations)
println 'Done!'



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
M


