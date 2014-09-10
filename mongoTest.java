package org.conan.mymahout.recommendation;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.mongodb.MongoDBDataModel;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class mongoTest {
	public static void main(String[] args) throws TasteException, IOException {
		String file = "datafile/item.csv";
        DataModel dbmodel = new FileDataModel(new File(file));
		DataModel dbmodel2 = new MongoDBDataModel("localhost", 27017,
				"test", "item", true, true, null);
				//"mahout", "ratings", true, true, null);
		Recommender svd = new SVDRecommender(dbmodel, new ALSWRFactorizer(dbmodel,3, 0.05f, 50));
		
		System.out.println();
		LongPrimitiveIterator users = dbmodel.getUserIDs();
		while (users.hasNext()) {
			long userId = users.nextLong();
			 System.out.println(userId);
			 LongPrimitiveIterator items = dbmodel.getItemIDs();
			 while ( items.hasNext()) {
				long itemId = items.nextLong();
				 //System.out.println(itemId);
				System.out.println(itemId+":"+svd.estimatePreference(userId, itemId));
			}
		}
		
		System.out.println();
		LongPrimitiveIterator users2 = dbmodel.getUserIDs();
        while (users2.hasNext()) {
            long uid = users2.nextLong();
            List<RecommendedItem> list = svd.recommend(uid, 2);
            System.out.printf("uid:%s", uid);
            for (RecommendedItem ritem : list) {
                System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
            }
            System.out.println();
        }

	}
}
