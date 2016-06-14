def serviceName = "avreg"
def buildTrigger = "NIGHTLY"

subJob = build  job: 'Gen2_CD_Pipeline',
            parameters: [
                        [$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
                        [$class: 'StringParameterValue', name: 'buildTrigger', value: buildTrigger ],
                ];
