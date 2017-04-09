library("forecast")

options(echo=FALSE) # if you want see commands in output file
args <- commandArgs(trailingOnly = TRUE)
print(args)
xDesc = "Time (Minutes)"
yDesc = "CPU"

outname=args[1]
forecastPoints = 5

predict <- function (x, doHoltWinters){
	#Set window size and slide
	win.size <- 15
	slide <- 1

	#Set up the table of results
	results <- data.frame(index = numeric(), win.mean = numeric())

	#i indexes the first value of the window
	i <- 1 + win.size
	#j indexes the row of the results to be added next
	j <- 1
	while(i < length(x)) {
		if(doHoltWinters) {
			datafore <- HoltWinters(x[(i-win.size):i], gamma=FALSE)
			data2 <- forecast.HoltWinters(datafore, h=forecastPoints)
			pres <- unlist(data2$mean)									# get result
			maxOffset <- unlist(lapply(pres, function(a) abs(x[i]-a)))	# compute delta between x[i] and every element of pres
			win.mean <- pres[which.max(maxOffset)]						# take the forecast with the highest delta (which.max gives idx of highest value)
		}
		else {
			datadiff <- x[(i-win.size):i] #diff(x[(i-win.size):i], difference=1)
			datadiffts <- ts(datadiff)									# create timeseries
			datadifftsarima <- arima(datadiff, order=c(2,1,0))			# predict arima
			datadifftsarimafc <- forecast.Arima(datadifftsarima, h=forecastPoints)	# make forecast
			pres <- unlist(datadifftsarimafc$mean)						# get result
			maxOffset <- unlist(lapply(pres, function(a) abs(x[i]-a)))	# compute delta between x[i] and every element of pres
			win.mean <- pres[which.max(maxOffset)]						# take the forecast with the highest delta (which.max gives idx of highest value)
		
		}
		#Insert the results
	    results[j, ] <- c(i, win.mean)
	    #Increment the indices for the next pass
	    i <- i + slide
	    j <- j + 1
	}
	return(results);
}

data <- scan(args[1])
data = data / 1000 #(data-min(data))/(max(data)-min(data)) 	# normalize data
resHoltWinters <- predict(data, 1)							# predict
datafore <- HoltWinters(data, gamma=FALSE)

data2 <- forecast.HoltWinters(datafore, h=forecastPoints)

out=paste(outname,"_holtwinters.pdf", sep="") 
pdf(out)
plot.forecast(data2, main="Holt-Winters Forecast", 
	xlab=xDesc, ylab=yDesc)
lines(resHoltWinters, col="blue")
grid (NULL,NULL, lty = "dashed") 

#ARIMA
datadiff <- diff(data, difference=1)	# for d parameter
datadiffts <- ts(data)					# timeseries
datadifftsarima <- arima(datadiffts, order=c(2,1,0))
datadifftsarimafc <- forecast.Arima(datadifftsarima, h=forecastPoints)

out=paste(outname,"_diff.pdf", sep="") 
pdf(out)
plot(datadiff, main="Plotted Difference", 
	xlab=xDesc, ylab=yDesc)
lines(datadiff)
#fcast <- (unlist(datadifftsarimafc$mean))
#tail(data, n=1)
#fcast <- c(tail(data, n=1), tail(data, n=1)+cumsum(fcast))
#fcast2 <- datadifftsarimafc #c(data, fcast)
res <- predict(data, 0)

out=paste(outname,"_arima.pdf", sep="") 
pdf(out)
plot(datadifftsarimafc, main="ARIMA Forecast", 
	xlab=xDesc, ylab=yDesc)#, xlim=c(0,30), ylim=c(0,1.5))
#lines(data, type="o")
lines(res, col="blue")
grid (NULL,NULL, lty = "dashed") 

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

arimaforecasts = padArray(res, data)
holtwintersforecasts = padArray(resHoltWinters, data)
out=paste(outname,"_predictions.csv", sep="") 
frame = data.frame(data, arimaforecasts, holtwintersforecasts)
write.table(frame, file = out, sep = ",", col.names = NA)
            
            
            
            
            
            
            


