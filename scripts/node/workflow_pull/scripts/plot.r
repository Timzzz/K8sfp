
#options(echo=FALSE)
args <- commandArgs(trailingOnly = TRUE)
print(args)

inname=args[1]
pdf(paste(inname,"_out.pdf",sep=""))
#pdf("asd.pdf")

mydata <- read.csv(inname)
mydata <- mydata/1000
print(seq(1, length(mydata[, 1])))
print(length(mydata[, 1]))
#plot(mydata[, 1], mydata[, -1])
matplot(seq(1, length(mydata[, 1])), mydata[, -1], type="l", xlab="Minutes", ylab="CPU")


