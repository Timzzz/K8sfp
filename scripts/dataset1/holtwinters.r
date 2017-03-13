library("forecast")
pdf("holtwinters_plot.pdf")

predict <- function (x){
	#Set window size and slide
	win.size <- 5
	slide <- 2

	#Set up the table of results
	results <- data.frame(index = numeric(), win.mean = numeric())

	#i indexes the first value of the window (the sill?)
	i <- 1 + win.size
	#j indexes the row of the results to be added next
	j <- 1
	while(i < length(x)) {
		datafore <- HoltWinters(x[(i-win.size):i], gamma=FALSE)
		data2 <- forecast.HoltWinters(datafore, h=5)
		maxd2 <- (lapply(data2$mean, function(x) x[which.max(abs(x))]))
		win.mean <- maxd2[[1]] #min(1, maxd2[[1]])
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
res <- predict(data)
datafore <- HoltWinters(data, gamma=FALSE)

#plot(datafore)
#lines(res)
data2 <- forecast.HoltWinters(datafore, h=5)
plot.forecast(data2, main="Data-Set 1: Holt-Winters Forecast", 
	xlab="Time", ylab="Memory Usage")
lines(res, col="blue")



