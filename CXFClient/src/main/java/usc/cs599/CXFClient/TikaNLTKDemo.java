package usc.cs599.CXFClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.ner.NamedEntityParser;
import org.apache.tika.parser.ner.nltk.NLTKNERecogniser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

public class TikaNLTKDemo
{
    static FileWriter file;
    static int i;
    static int size;
    @SuppressWarnings("unchecked")
    public static void main( String[] args )throws IOException, ParseException, TikaException, SAXException
    {
        try{
            String url = "URL";
            Response response = WebClient.create(url, "username", "password", null).accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get();
            file = new FileWriter("index.100text.dump");
            file.write("{\"docs\":[");
            String resp = response.readEntity(String.class);
            JSONParser parser = new JSONParser();
            JSONObject a = (JSONObject) parser.parse(resp);
            JSONArray result = (JSONArray) ((JSONObject) a.get("response")).get("docs");
            //			JSONArray ner_result = new JSONArray();
            
            System.setProperty(NamedEntityParser.SYS_PROP_NER_IMPL, NLTKNERecogniser.class.getName());
            Metadata md = new Metadata();
            Tika t1 = new Tika(new TikaConfig(new File("tika-config.xml")));
            JSONObject o;
            final long startTime = System.currentTimeMillis();
            size = result.size();
            for(i = 0; i<result.size(); i++){
                o = new JSONObject();
                String test = (String) ((JSONObject)result.get(i)).get("content");
                String id = (String) ((JSONObject)result.get(i)).get("id");
                o.put("id", id);
                o.put("content", test);
                md = new Metadata();
                
                t1.parse(new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8)), md);
                
                
                if(md.getValues("NER_GPE").length>0)
                    o.put("ner_gpe", Arrays.asList(md.getValues("NER_GPE")));
                if(md.getValues("NER_LOCATION").length>0)
                    o.put("ner_location", Arrays.asList(md.getValues("NER_LOCATION")));
                if(md.getValues("NER_PERCENT").length>0)
                    o.put("ner_percent", Arrays.asList(md.getValues("NER_PERCENT")));
                if(md.getValues("NER_TIME").length>0)
                    o.put("ner_time", Arrays.asList(md.getValues("NER_TIME")));
                if(md.getValues("NER_FACILITY").length>0)
                    o.put("ner_facility", Arrays.asList(md.getValues("NER_FACILITY")));
                if(md.getValues("NER_DATE").length>0)
                    o.put("ner_date", Arrays.asList(md.getValues("NER_DATE")));
                if(md.getValues("NER_MONEY").length>0)
                    o.put("ner_money", Arrays.asList(md.getValues("NER_MONEY")));
                if(md.getValues("NER_ORGANIZATION").length>0)
                    o.put("ner_organisation", Arrays.asList(md.getValues("NER_ORGANIZATION")));
                if(md.getValues("NER_PERSON").length>0)
                    o.put("ner_person", Arrays.asList(md.getValues("NER_PERSON")));
                if(md.getValues("NER_NE").length>0)
                    o.put("ner_ne", Arrays.asList(md.getValues("NER_NE")));
                //		        ner_result.add(o);
                System.out.println(i);
                file.write(o.toJSONString());
                if(i!=size-1){
                    file.write(",");
                }
                
            }
            //			file.write(ner_result.toJSONString());
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime)/1000 );
            System.out.println("done");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally{
            if(i<size-1){
                file.write("{}");
            }
            file.write("]}");
            file.close();
        }	
    }	
}
