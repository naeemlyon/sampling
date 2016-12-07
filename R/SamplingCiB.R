##################################################
## *************   written by   *************** ##
## ************* Muhammad Naeem *************** ##
## ******* Muhammad.Naeem@univ.lyon2.fr ******* ##
##################################################

initializeLibraries <- function(x=1) {
  
  if (x < 1) { x <-1 }
  x <- x * 512
  ram <- paste("-Xmx", as.factor(x),"m", sep = "") 

  pth <- path.package(package = "SamplingCiB")
  pth <- paste(pth, "/java/", sep="")

  .jinit(pth, parameters= ram) # folder containing .class and .jar
  print ( paste("jvm initilized by", ram, sep=" : "))   

  .jaddClassPath(paste(pth,"commons-io-2.4.jar", sep = "")) 
  .jaddClassPath(paste(pth,"commons-math3-3.5.jar", sep = "")) 
  .jaddClassPath(paste(pth,"commons-lang3-3.3.2.jar", sep = ""))
}

#################################################

showClassPath <- function() {
  .jclassPath()
}  

#################################################

objectClass <- function(input) {
  obj <- .jnew(input)
  return (obj)
}  

#################################################

setFileToSample <- function(obj, input) {
  .jcall(obj, "V", "setFileToSample", input)
}  

#################################################

setOverSampleFile <- function(obj, input) {
  .jcall(obj, "V", "setOverSampleFile", input)
}  

#################################################

setClassOfInterest <- function(obj, input) {
  .jcall(obj, "V", "setCoI", input)
}  

#################################################

setHeaderInOverSample <- function(obj, input) {
  .jcall(obj, "V", "setcopyHeaderInOverSample", input)
}  

#################################################

setUseGaussianDist <- function(obj, input) {
  .jcall(obj, "V", "setuseGaussianDist", input)
}  

#################################################

setK <- function(obj, input) {
  .jcall(obj, "V", "setK", as.integer(input))
}  

#################################################

setIncreaseFactor <- function(obj, input) {
  .jcall(obj, "V", "setIncreaseFactor", as.double(input))
}  

#################################################

setWindowSize <- function(obj, input) {
  .jcall(obj, "V", "setWindowSize", as.integer(input))
}  

#################################################

showParamList <- function(obj) {
  paramList <- .jcall(obj, "[S", "ListParameters")
  return (paramList)
}  

#################################################

doSampling <- function(obj) {
  runStatus <- .jcall(obj, "[S", "Run")
  return (runStatus)
}  

#################################################