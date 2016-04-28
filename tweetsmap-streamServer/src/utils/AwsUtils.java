package utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

public class AwsUtils {
	
	private static final AWSCredentials credentials = new ProfileCredentialsProvider("Jiaming").getCredentials();
	private static final Region usEast1 = Region.getRegion(Regions.US_EAST_1);
	
	public static AWSCredentials getCredentials() {
		return credentials;
	}
	
	public static Region getRegion() {
		return usEast1;
	}


}
