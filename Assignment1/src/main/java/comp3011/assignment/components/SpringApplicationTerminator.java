package comp3011.assignment.components;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationTerminator implements ApplicationTerminator {
	private final ApplicationContext context;

    public SpringApplicationTerminator(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void terminate() {
        // Delay 500ms and close on a separate thread so the 202 response is sent first.
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            ((ConfigurableApplicationContext) context).close();
        }).start();
    }
}
