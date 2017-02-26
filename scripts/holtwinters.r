library("forecast")
pdf("holtwinters_plot.pdf")

predict <- function (x, y){
	#Set window size and slide
	win.size <- 16
	slide <- 2

	#Set up the table of results
	results <- data.frame(index = numeric(), win.mean = numeric())

	#i indexes the first value of the window (the sill?)
	i <- 1 + win.size
	#j indexes the row of the results to be added next
	j <- 1
	while(i < length(x)) {
		if(y) {
			datafore <- HoltWinters(x[(i-win.size):i], gamma=FALSE)
			data2 <- forecast.HoltWinters(datafore, h=5)
			maxd2 <- (lapply(data2$mean, function(x) x[which.max(abs(x))]))
			win.mean <- maxd2[[1]] #min(1, maxd2[[1]])
		}
		else {
			datadiff <- diff(x[(i-win.size):i], difference=1)
			datadiffts <- ts(datadiff)
			print(datadiffts)
			datadifftsarima <- arima(datadiffts, order=c(2,0,0))
			datadifftsarimafc <- forecast.Arima(datadifftsarima, h=5)
			maxd2 <- (lapply(datadifftsarimafc$mean, function(x) x[which.max(abs(x))]))
			win.mean <- x[i]+maxd2[[1]]
		}
		#Insert the results
	    results[j, ] <- c(i, win.mean)
	    #Increment the indices for the next pass
	    i <- i + slide
	    j <- j + 1
	}
	return(results);
}


data <- scan("eventlog.csv.extract.csv")
data = (data-min(data))/(max(data)-min(data))
res <- predict(data, 1)
datafore <- HoltWinters(data, gamma=FALSE)

data2 <- forecast.HoltWinters(datafore, h=5)
#plot.forecast(data2, main="Data-Set 1: Holt-Winters Forecast", 
#	xlab="Time", ylab="Memory Usage")
#lines(res, col="blue")

#ARIMA
datadiff <- diff(data, difference=1)
datadiffts <- ts(datadiff)
datadifftsarima <- arima(datadiffts, order=c(2,0,0))
datadifftsarimafc <- forecast.Arima(datadifftsarima, h=5)
#plot.forecast(datadifftsarimafc, main="Data-Set 1: ARIMA Forecast", 
#	xlab="Time (Minutes)", ylab="Memory Usage (Difference)")
fcast <- (unlist(datadifftsarimafc$mean))
tail(data, n=1)
fcast <- c(tail(data, n=1), tail(data, n=1)+cumsum(fcast))
fcast2 <- c(data, fcast)
res <- predict(data, 0)
plot(fcast2, main="Data-Set 1: ARIMA Forecast", 
	xlab="Time (Minutes)", ylab="Memory Usage")#, xlim=c(0,30), ylim=c(0,1.5))
lines(data, type="o")
lines(res, col="blue")
