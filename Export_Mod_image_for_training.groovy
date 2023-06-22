// **** Save 32bit multichannel ICW image as OMETiff in QuPath 0.4.0_   ****

import qupath.lib.images.ImageData
import qupath.lib.images.servers.ConcatChannelsImageServer
import qupath.lib.images.servers.TransformedServerBuilder
import javafx.application.Platform
import qupath.lib.scripting.QP
import qupath.lib.projects.Project
import qupath.lib.gui.scripting.QPEx
import qupath.lib.images.writers.ome.OMEPyramidWriter

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

//  ****** PARAMETERS *********

def outpath = "/gpfs/gsfs11/users/perezriverosp/combine images/"

// OME.Tiff parameters
def tilesize = 512
def outputDownsample = 1
def pyramidscaling = 4
def nThreads = 4
def compression = OMEPyramidWriter.CompressionType.UNCOMPRESSED  //ZLIB //UNCOMPRESSED //LZW  (not working with 32bit: //J2K_LOSSY //J2K)

//  ***************************

/////////////////////////def imageData = QPEx.getCurrentViewer().getImageData()
///////////////////////////def server = imageData.getServer()
def name = getProjectEntry().getImageName()

def filename = name + "combined.ome.tif"
def pathOutput = outpath + filename

println('Writing OME-TIFF ' + filename)

new OMEPyramidWriter.Builder(combined)
    .compression(compression)
    .parallelize(nThreads)
    .channelsInterleaved()     
    .tileSize(tilesize)
    .scaledDownsampling(outputDownsample, pyramidscaling)
    .build()
    .writePyramid(pathOutput)

println('Done:' + filename)