import qupath.lib.images.servers.LabeledImageServer

def imageData = getCurrentImageData()

// Define output path (relative to project)
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def pathOutput = buildFilePath(PROJECT_BASE_DIR, 'export', name + "mask" + '.ome.tif')
mkdirs(pathOutput)

// Define downsample
double downsample = 1

// Create an ImageServer where the pixels are derived from cells
// If the downsample isn't 1, objects might still touch
def labelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0)
    .useCells()
    .useInstanceLabels()      
    .multichannelOutput(false)
  //  .useFilter(p -> p.isAnnotation() && p.getPathClass() == getPathClass('Cell1'))
    .build()
    
writeImage(labelServer, pathOutput)
print "DONE"