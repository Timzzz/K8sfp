library("forecast")

options(echo=FALSE)
args <- commandArgs(trailingOnly = TRUE)
print(args)
xDesc = "Time (Minutes)"
yDesc = "CPU"

outname=args[1]
forecastPoints = 5
windowSize = 60
startingPoint = 10

createForecast <- function(data, doHoltWinters) {
	if(doHoltWinters) {
			data <- HoltWinters(data, gamma=FALSE)
			data <- forecast.HoltWinters(data, h=forecastPoints)
			return(data)
		}
		else {
			data <- auto.arima(data)
			data <- forecast.Arima(data, h=forecastPoints)
			return(data)
		}	
}

getFcData <- function(data, doHoltWinters, mode) {
	data <- createForecast(data, doHoltWinters)
	if(mode == 0) return(unlist(data$lower))
	else if(mode == 1) return(unlist(data$mean))		
	else if(mode == 2) return(unlist(data$upper))		
}

# Sliding Window forecast
predict <- function (x, doHoltWinters, mode){
	win.size <- windowSize
	slide <- 1

	resultsL <- data.frame(index = numeric(), win.mean = numeric())
	results <- data.frame(index = numeric(), win.mean = numeric())
	resultsU <- data.frame(index = numeric(), win.mean = numeric())
	i <- startingPoint
	j <- 1
	while(i <= length(x)) {
		data <- ts(x[max(0,i-win.size):i])
		data <- createForecast(data, doHoltWinters)
		resLower <- data$lower[forecastPoints]
		res <- data$mean[forecastPoints]
		resUpper <- data$upper[forecastPoints]
		print(res)
		idx <- i #+ forecastPoints
		resultsL[j, ] <- c(idx, resLower)
	    results[j, ] <- c(idx, res)
	    resultsU[j, ] <- c(idx, resUpper)
	    i <- i + slide
	    j <- j + 1
	}
	print(results)
	return(list("lower" = resultsL, "mean" = results, "upper" = resultsU));
}

data <- scan(args[1])
data = data / 1000

# HOLTWINTERS
resHW <- (predict(data, 1, 0))
datahw <- createForecast(data, 1)
out=paste(outname,"_holtwinters.pdf", sep="")  
pdf(out)
plot.forecast(datahw, main="Holt-Winters Forecast",  xlab=xDesc, ylab=yDesc, ylim=c(0,1.5))
lines(data, type="o")
lines(resHW$lower, col="gray30", lty=2)
lines(resHW$mean, col="blue")
lines(resHW$upper, col="gray30", lty=2)
grid (NULL,NULL, lty = "dashed") 

#ARIMA
print("ARIMA")
res <- predict(data, 0, 1)
dataarima <- createForecast(data, 0)
out=paste(outname,"_arima.pdf", sep="")  
pdf(out)
plot(dataarima, main="ARIMA Forecast", xlab=xDesc, ylab=yDesc, ylim=c(0,1.5))
lines(data, type="o")
lines(res$lower, col="gray30", lty=2)
lines(res$mean, col="blue")
lines(res$upper, col="gray30", lty=2)
grid (NULL,NULL, col="gray", lty=2) 

#print(dataarima)
#print(unlist(dataarima$mean))
#str(dataarima)

### WRITE TO CSV
padArray <- function(array, arrayForFill) {
	i=1	# filling up leading / trailing zeros ..
	j=1
	res = c(arrayForFill)
	while(i<length(arrayForFill)) {
		res[i] = arrayForFill[i]
		if(array[j, 1] == i) {
			res[i] = array[j, 2]
			j = j+1
		}
		i = i+1
	}
	return(res);
}

arimaforecasts = padArray(res$mean, data)
holtwintersforecasts = padArray(resHW$mean, data)
out=paste(outname,"_predictions.csv", sep="") 
frame = data.frame(data, arimaforecasts, holtwintersforecasts)
write.table(frame, file = out, sep = ",", col.names = NA)
            
            
            
            
            
            
            


