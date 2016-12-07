library(rJava)
library(SamplingCiB)
cat("\014")  

initializeLibraries(12)  

showClassPath()
obj <- objectClass("oversampling.Sampling")
  
paramList <- showParamList(obj)
paramList;




FileToSample <- system.file("extdata", "dermatology", package="SamplingCiB")
setFileToSample(obj, FileToSample)  

OverSampleFile <- "os"
setOverSampleFile(obj, OverSampleFile) 

CoI <- ""
setClassOfInterest(obj, CoI)  

HeaderInOverSample <- TRUE
setHeaderInOverSample(obj, HeaderInOverSample)
  
useGuassianDist = FALSE
setUseGuassianDist(obj, useGuassianDist)  

k <-4
setK(obj, k)

IncreaseFactor <- 0.0
setIncreaseFactor(obj, IncreaseFactor)

paramList <- showParamList(obj)
paramList;

doSampling(obj)


# remove.packages("SamplingCiB", lib="~/R/x86_64-pc-linux-gnu-library/3.2")

