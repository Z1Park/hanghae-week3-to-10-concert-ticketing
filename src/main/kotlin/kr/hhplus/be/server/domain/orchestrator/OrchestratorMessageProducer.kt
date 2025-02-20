package kr.hhplus.be.server.domain.orchestrator

interface OrchestratorMessageProducer {

	fun sendRollbackPayReservationMessage(traceId: String)
}