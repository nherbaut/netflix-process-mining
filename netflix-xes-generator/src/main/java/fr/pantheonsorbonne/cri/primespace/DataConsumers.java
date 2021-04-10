package fr.pantheonsorbonne.cri.primespace;

import java.io.IOException;
import java.time.Instant;
import java.util.function.BiConsumer;

import javax.xml.datatype.DatatypeConfigurationException;

import fr.pantheonsorbonne.cri.model.stream4good.Session;
import fr.pantheonsorbonne.cri.model.stream4good.Thumbnail;
import fr.pantheonsorbonne.cri.model.stream4good.ThumbnailData;
import fr.pantheonsorbonne.cri.model.stream4good.UserData;
import fr.pantheonsorbonne.cri.model.stream4good.Watch;
import fr.pantheonsorbonne.cri.model.stream4good.WatchData;
import fr.pantheonsorbonne.ufr27.miage.model.xes.Log;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

class DataConsumers {

	


	
	
	static class UserDataConsumer implements BiConsumer<Log, UserData>{

		@Override
		public void accept(Log t, UserData u) {
			// TODO Auto-generated method stub
			
		}
		
	}

	

}