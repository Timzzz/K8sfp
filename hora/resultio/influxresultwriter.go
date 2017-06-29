package resultio

import (
	"log"

	"../cfp"
	"../fpm"

	"github.com/influxdata/influxdb/client/v2"
	//"github.com/spf13/viper"
)

type InfluxResultWriter struct {
	influxClnt client.Client
}

func New(addr, username, password, db string) (InfluxResultWriter, error) {
	var influxResultWriter InfluxResultWriter
	clnt, err := client.NewHTTPClient(client.HTTPConfig{
		Addr:     addr,
		Username: username,
		Password: password,
	})
	if err != nil {
		log.Printf("result-writer: cannot create new influxdb client. %s", err)
		return influxResultWriter, err
	}
	influxResultWriter.influxClnt = clnt

	err = influxResultWriter.createDB(db)
	if err != nil {
		log.Printf("result-writer: cannot create DB. %s", err)
		return influxResultWriter, err
	}

	return influxResultWriter, nil
}

func (w *InfluxResultWriter) createDB(db string) error {
	q := client.Query{
		Command:  "CREATE DATABASE " + db,
		Database: db,
	}
	if response, err := w.influxClnt.Query(q); err == nil {
		if response.Error() != nil {
			return response.Error()
		}
	} else {
		return err
	}
	return nil
}

func (w *InfluxResultWriter) WriteCfpResult(result cfp.Result) error {
	// Create a new point batch
	bp, err := client.NewBatchPoints(client.BatchPointsConfig{
		Database:  "hora",
		Precision: "ns",
	})
	if err != nil {
		log.Printf("result-writer: cannot create batch point. %s", err)
		return err
	}

	// Create a point and add to batch
	tags := map[string]string{
		"name":     result.Component.Name,
		"hostname": result.Component.Hostname,
		"type":     result.Component.Type,
	}
	fields := map[string]interface{}{
		"failureProbability": result.FailProb,
	}

	pt, err := client.NewPoint("cfp", tags, fields, result.Predtime)
	if err != nil {
		log.Printf("result-writer: cannot create point. %s", err)
		return err
	}
	bp.AddPoint(pt)

	// Write the batch
	if err := w.influxClnt.Write(bp); err != nil {
		log.Printf("result-writer: cannot write batch point. %s", err)
		return err
	}
	return nil
}

func (w *InfluxResultWriter) WriteFpmResult(result fpm.Result) error {
	// Create a new point batch
	bp, err := client.NewBatchPoints(client.BatchPointsConfig{
		Database:  "hora",
		Precision: "ns",
	})
	if err != nil {
		log.Printf("result-writer: cannot create batch point. %s", err)
		return err
	}

	for k, v := range result.FailProbs {
		// Create a point and add to batch
		tags := map[string]string{
			"name":     k.Name,
			"hostname": k.Hostname,
			"type":     k.Type,
		}
		fields := map[string]interface{}{
			"failureProbability": v,
		}

		pt, err := client.NewPoint("fpm", tags, fields, result.Predtime)
		if err != nil {
			log.Printf("result-writer: cannot create point. %s", err)
			return err
		}
		bp.AddPoint(pt)
	}

	// Write the batch
	if err := w.influxClnt.Write(bp); err != nil {
		log.Printf("result-writer: cannot write batch point. %s", err)
		return err
	}
	return nil
}
