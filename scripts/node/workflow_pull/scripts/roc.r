
#INPUT: 
# <PREDICTION FILE>
# <SUCCESSES FILE>
# <FAILS FILE>
# Files contain comma-separated csv columns

simple_roc <- function(labels, scores){
  labels <- labels[order(scores, decreasing=TRUE)]
  data.frame(TPR=cumsum(labels)/sum(labels), FPR=cumsum(!labels)/sum(!labels), labels)
}

calcMetric <- function(ar, fm, arima_factor, th){
	arm <- (ar - arima_factor)#/(1-arima_factor)
	arm[arm>1] <- 1
	arm[arm<0] <- 0
	arm[is.na(arm)] <- 0
	
	lngth <- length(fm[,1])
	mean <-   array(1:lngth)
	meanfm <- array(1:lngth)
	
	for(i in 1:lngth) {
		mean[i] = sum(arm[i,])/length(arm[i,])
		meanfm[i] = sum(fm[i,])/length(fm[i,])
	}
	return(list(ARM=arm, FM=fm, AVG=mean, AVGFM=meanfm))
}

# run = predicted execution values
# failure_metric = actual execution values
calcRates <- function(run, failure_metric, th) {
	fm <- array(1:length(run))	# make fm only > 0 if a failure happened in this time-span
	for(i in 2:length(failure_metric)) {
		fm[i] = failure_metric[i]-failure_metric[i-1]
	}
	failure_metric <- fm
	run <- run>th	
	failure_metric <- failure_metric>th
	
	tp <- array(1:length(run))
	tn <- array(1:length(run))
	fp <- array(1:length(run))
	fn <- array(1:length(run))
	
	# TP Rates
	for(i in 1:length(run)) {
		#print(paste(i, run[i], failure_metric[i], sep=","))
		tp[i] = run[i] == 1 && failure_metric[i] == 1
		tn[i] = run[i] == 0 && failure_metric[i] == 0
		fp[i] = run[i] == 1 && failure_metric[i] == 0
		fn[i] = run[i] == 0 && failure_metric[i] == 1
	}
	tpr <- cumsum(tp)/(sum(tp)+sum(fn))
	fpr <- cumsum(fp)/(sum(fp)+sum(tn))
	
	list(TP=tp, TN=tn, FP=fp, FN=fn, TPR=tpr, FPR=fpr)
}

#options(echo=FALSE)
args <- commandArgs(trailingOnly = TRUE)
print(args)

in_arima=args[1]
in_suc=args[2]
in_fails=args[3]

pdf(paste(in_arima,"_out.pdf",sep=""))
#pdf("asd.pdf")

ar <- read.csv(in_arima)
su <- read.csv(in_suc)
fa <- read.csv(in_fails)

arima_factor=0.9
arima_factor_old=arima_factor
th=0.0001

# FAILS Metric
fm <- fa/(fa+su)
fm[is.na(fm)] <- 0

ar[ar>1] <- 1
ar[ar<0] <- 0

metric <- calcMetric(ar, fm, arima_factor, th)
#rates <- calcRates(metric$ARM[,1], metric$FM[,1])
rates <- calcRates(metric$AVG, metric$AVGFM, th)

doit=FALSE
if(doit==TRUE){
	#arima_factor=0
	len=10
	tpr <- array(1:len+1)
	fpr <- array(1:len+1)
	
	#nums <- as.numeric(c("0", "0.0001", "0.001", "0.01", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "0.95", "0.96", "0.97", "0.985", "0.987", "0.99", "0.995", "0.997", "0.999", "1"))
	#len=length(nums)
	plot(1, type="o", col="red", ylim=c(0,1), xlim=c(0,1), xlab="False Positive Rate", ylab="True Positive Rate")
	for(i in 1:(len+1)){	# alternate arima threshold
		arima_factor=((i-1)/len)
		#th = ((i-1)/(len*100))
		print(paste(arima_factor, th))
		metric <- calcMetric(ar, fm, arima_factor, th)
		rates  <- calcRates(metric$AVG, metric$AVGFM, th)
		tpr[i]=rates$TPR[length(rates$TPR)]
		fpr[i]=rates$FPR[length(rates$FPR)]
		print(max(rates$TPR))
		lines(rates$FPR, rates$TPR, col="gray")
		points(rates$FPR, rates$TPR, col="gray")
	}
	lines(fpr, tpr, type="l", col="blue")
	points(fpr, tpr, type="p", col="red")
	
}
arima_factor=arima_factor_old

par <- metric$ARM
matplot(seq(1, length(par[, 1])), par[, -1], type="l", xlab="Minutes", ylab="Predicted Failure Probability")
par <- metric$FM
matplot(seq(1, length(par[, 1])), par[, -1], type="l", xlab="Minutes", ylab="Actual Failure Percentage")

plot(metric$AVG, type="l")
lines(metric$AVGFM)
lines(metric$ARM[,1], col="gray")

plot(rates$FPR, rates$TPR, ylim=c(0,1), xlim=c(0,1), type="o")

