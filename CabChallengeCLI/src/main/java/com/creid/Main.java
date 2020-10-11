package com.creid;

import org.apache.commons.cli.*;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

import com.creid.OperationBuilder;

public class Main {

    public static void main(String[] args) throws IOException  {

        Options options = new Options();
        OperationBuilder operationBuilder = new OperationBuilder();

        Option medallions = new Option("m", "medallions ", true, "medallion codes");
        medallions.setRequired(false);
        options.addOption(medallions);

        Option tripDate = new Option("d", "date ", true, "trip date");
        tripDate.setRequired(false);
        options.addOption(tripDate);

        Option ignoreCache = new Option("ic", "ignoreCache ", false, "ignore cache");
        options.addOption(ignoreCache);

        Option clearCache = new Option("cc", "clearCache ", false, "clear cache");
        options.addOption(clearCache);

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd;

            cmd = parser.parse(options, args);

            String tripDateArg = cmd.getOptionValue("d");
            String medallionsArg = cmd.getOptionValue("m");

            //Handle invalid arguments
            if (tripDateArg != null && medallionsArg == null) {
                throw new ParseException("Missing required arg -m or --medallions");
            } else if (medallionsArg != null && tripDateArg == null) {
                throw new ParseException("Missing required arg -d or --date");
            } else if (medallionsArg != null && cmd.hasOption("cc")) {
                throw new ParseException("Invalid params. Perform -cc or --clearCache as a separate operation");
            }

            //Perform operations
            if (tripDateArg != null  && medallionsArg != null) {
                //Send medalliontrips operation
                HttpResponse medallionTripsResponse = operationBuilder.sendMedalliontripsRequest(tripDateArg, medallionsArg, cmd.hasOption("ic"));
                outputResponse(medallionTripsResponse);
            } else if (cmd.hasOption("cc")) {
                //Send clearcache operation
                System.out.println(operationBuilder.sendClearCacheRequest());
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        System.out.println("Done");
    }

    static void outputResponse(HttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
