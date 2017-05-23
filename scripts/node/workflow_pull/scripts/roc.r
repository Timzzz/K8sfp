
#INPUT: 
# <PREDICTION FILE>
# <SUCCESSES FILE>
# <FAILS FILE>
# Files contain comma-separated csv columns

simple_roc <- function(labels, scores){
  labels <- labels[order(scores, decreasing=TRUE)]
  data.frame(TPR=cumsum(labels)/sum(labels), FPR=cumsum(!labels)/sum(!labels), labels)
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

arima_factor=0.7
threshold=0.0001

# ARIMA Metric
arm <- (ar - arima_factor)/(1-arima_factor)
arm[arm>1] <- 1
arm[arm<0] <- 0
arm1 <- arm
arm <- arm>threshold

# FAILS Metric
fm <- fa/(fa+su)
fm[is.na(fm)] <- 0
fm1 <- fm
fm <- fm>threshold

# TP Rates
tp <- fm + arm == 2
tn <- fm == 0 && arm == 0
fp <- fm == 0 && arm == 1
fn <- fm == 1 && arm == 0

par <- arm1
matplot(seq(1, length(par[, 1])), par[, -1], type="l", xlab="Minutes", ylab="Predicted Failure Probability")
par <- fm1
matplot(seq(1, length(par[, 1])), par[, -1], type="l", xlab="Minutes", ylab="Actual Failure Percentage")

roc <- tp
plot(roc, main="ROC Curve", xlab="False Positive Rate", ylab="True Positive Rate", ylim=c(0,1))
for(i in 1:length(tp[1,])) {
	print(i)
	x <- simple_roc(tp[,i], arm[,i])
	lines(x$FPR, x$TPR, type="o")
}

