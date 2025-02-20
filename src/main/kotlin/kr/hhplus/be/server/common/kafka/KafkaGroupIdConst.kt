package kr.hhplus.be.server.common.kafka

class KafkaGroupIdConst {

	companion object {
		const val GROUP_TEST = "test"
		const val GROUP_CONCERT = "concert"
		const val GROUP_RESERVATION = "reservation"
		const val GROUP_DATA_PLATFORM = "dataPlatform"
		const val GROUP_DLQ = "deadLetterQueue"
	}
}