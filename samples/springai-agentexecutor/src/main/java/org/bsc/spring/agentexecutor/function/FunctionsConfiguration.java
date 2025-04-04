package org.bsc.spring.agentexecutor.function;

import org.bsc.spring.agentexecutor.function.math.AddFunction;
import org.bsc.spring.agentexecutor.function.weather.WeatherConfig;
import org.bsc.spring.agentexecutor.function.weather.WeatherFunction;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for various functions related to weather.
 * This configuration class provides methods to define and execute weather-related functions.
 */
@Configuration
public class FunctionsConfiguration {

    /**
     * Weather configuration properties.
     */
    private final WeatherConfig props;

    /**
     * Constructor for creating a new instance of FunctionsConfiguration.
     * @param props Weather configuration properties.
     */
    public FunctionsConfiguration(WeatherConfig props) {
        this.props = props;
    }

    /**
     * Bean method to define the weather function callback.
     * @return A FunctionCallback object that wraps a WeatherFunction and provides metadata about it.
     */
    @Bean
    public ToolCallback weatherToolCallback() {

        return FunctionToolCallback.builder( "weatherFunction",  new WeatherFunction(props) )
                .description("Get the weather in location") // (2) function description
                .inputType(WeatherFunction.Request.class)
                .build();
    }

    /**
     * Bean method to define the weather function callback.
     * @return A FunctionCallback object that wraps a WeatherFunction and provides metadata about it.
     */
    @Bean
    public ToolCallback addToolCallback() {

        return FunctionToolCallback.builder( "addFunction",  new AddFunction() )
                .description("sum two numbers") // (2) function description
                .inputType(AddFunction.Request.class)
                .build();
    }




}
