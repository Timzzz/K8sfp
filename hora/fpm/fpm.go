package fpm

import (
	"time"

	"../adm"
	"../cfp"
)

type Fpm interface {
	UpdateAdm(adm.ADM)
	UpdateCfpResult(cfpResult cfp.Result)
}

type Result struct {
	FailProbs map[adm.Component]float64
	Timestamp time.Time
	Predtime  time.Time
}
