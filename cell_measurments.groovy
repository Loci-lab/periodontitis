import qupath.lib.analysis.features.ObjectMeasurements

def measurements = ObjectMeasurements.Measurements.values() as List
def compartments = ObjectMeasurements.Compartments.values() as List // Won't mean much if they aren't cells...
def server = getCurrentServer()
def downsample = 1.0
for (detection in getDetectionObjects()) {
  ObjectMeasurements.addIntensityMeasurements(
      server, detection, downsample, measurements, compartments
      )
}