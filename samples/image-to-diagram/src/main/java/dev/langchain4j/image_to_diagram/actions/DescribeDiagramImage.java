package dev.langchain4j.image_to_diagram.actions;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.image_to_diagram.state.Diagram;
import dev.langchain4j.image_to_diagram.DiagramOutputParser;
import dev.langchain4j.image_to_diagram.ImageToDiagram;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.NonNull;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

import static dev.langchain4j.image_to_diagram.ImageToDiagram.loadPromptTemplate;

/**
 * This class implements the `NodeAction` interface for diagram image description.
 * It provides functionality to process the state of an image and generate a textual description based on its content using a vision model.
 */
public class DescribeDiagramImage implements NodeAction<ImageToDiagram.State> {
    /**
     * The language model responsible for generating the description.
     */
    final ChatLanguageModel visionModel;

    /**
     * Constructs a `DescribeDiagramImage` instance with the given language model.
     *
     * @param visionModel The language model used to generate descriptions.
     */
    public DescribeDiagramImage( @NonNull ChatLanguageModel visionModel) {
        this.visionModel = visionModel;
    }

    /**
     * Applies the provided image to create a diagram.
     *
     * @param state The current state of the image to be converted into a diagram.
     * @return A map containing the resulting diagram and associated image data.
     * @throws Exception if no image data is provided in the state.
     */
    @Override
    public Map<String, Object> apply(ImageToDiagram.State state) throws Exception {
        // Load the prompt template for diagram image description
        dev.langchain4j.model.input.Prompt systemPrompt = loadPromptTemplate( "describe_diagram_image.txt" )
                .apply( Map.of() );

        // Retrieve the image data from the state, throw an exception if no data is available
        var imageUrlOrData = state.imageData().orElseThrow( () -> new RuntimeException("No image data provided")) ;

        // Determine the type of image content based on whether it's a URL or binary data
        var imageContent = (imageUrlOrData.url()!=null) ?
                ImageContent.from(imageUrlOrData.url(), ImageContent.DetailLevel.AUTO) :
                ImageContent.from(imageUrlOrData.data(), "image/png", ImageContent.DetailLevel.AUTO);
        // Create text content based on the system prompt
        var textContent = new TextContent(systemPrompt.text());
        // Combine the image and text content into a user message
        var message = UserMessage.from(textContent, imageContent);

        // Generate a response using the vision model
        dev.langchain4j.model.output.Response<AiMessage> response = visionModel.generate( message );

        // Parse the response to extract diagram elements
        DiagramOutputParser outputParser = new DiagramOutputParser();

        Diagram.Element result = outputParser.parse( response.content().text() );

        // Return a map containing the result diagram and image data
        return Map.of( "diagram",result, "imageData", new Object() );
    }
}