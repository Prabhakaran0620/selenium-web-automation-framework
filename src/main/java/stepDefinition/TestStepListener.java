package stepDefinition;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;

public class TestStepListener implements EventListener {

	@Override
	public void setEventPublisher(EventPublisher publisher) {
		publisher.registerHandlerFor(TestStepStarted.class, this::handleTestStepStarted);
		publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
	}

	private void handleTestStepStarted(TestStepStarted event) {
		if (event.getTestStep() instanceof PickleStepTestStep) {
			PickleStepTestStep testStep = (PickleStepTestStep) event.getTestStep();
			String stepText = testStep.getStep().getText();
			String keyword = testStep.getStep().getKeyword();
			
			// Log to console
			System.out.println(keyword + stepText);
		}
	}

	private void handleTestStepFinished(TestStepFinished event) {
		if (event.getTestStep() instanceof PickleStepTestStep) {
			PickleStepTestStep testStep = (PickleStepTestStep) event.getTestStep();
			String stepText = testStep.getStep().getText();
			String keyword = testStep.getStep().getKeyword();
			String fullStep = keyword + stepText;
			
			// Log step to Extent Report
			if (Hooks.test != null) {
				io.cucumber.plugin.event.Status status = event.getResult().getStatus();
				
				switch (status) {
					case PASSED:
						Hooks.test.pass(fullStep);
						break;
					case FAILED:
						Hooks.test.fail(fullStep);
						if (event.getResult().getError() != null) {
							Hooks.test.fail(event.getResult().getError());
						}
						break;
					case SKIPPED:
						Hooks.test.skip(fullStep);
						break;
					default:
						Hooks.test.info(fullStep);
						break;
				}
			}
		}
	}
}