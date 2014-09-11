package org.conan.mymahout.recommendation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.mongodb.MongoDBDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.*;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class mongoTest {
	public static void main(String[] args) throws TasteException, IOException {
		String file = "datafile/item.csv";
        DataModel dbmodel2 = new FileDataModel(new File(file));
		DataModel dbmodel = new MongoDBDataModel("localhost", 27017,
				"test", "items", true, true, null);//,"user_id","item_id","preference");
		
		//Recommender r2 = new 
		System.out.println("EuclideanDistanceSimilarity");
	    final int NEIGHBORHOOD_NUM = 2;
	    final int RECOMMENDER_NUM = 3;
		UserSimilarity user = new EuclideanDistanceSimilarity(dbmodel);
        NearestNUserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, user, dbmodel);
        
        Recommender r = new GenericUserBasedRecommender(dbmodel, neighbor, user); //userbase
        LongPrimitiveIterator iter = dbmodel.getUserIDs();
        while (iter.hasNext()) {
            long uid = iter.nextLong();
            List<RecommendedItem> list = r.recommend(uid, RECOMMENDER_NUM);
            System.out.printf("uid:%s", uid);
            for (RecommendedItem ritem : list) {
                System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
            }
            System.out.println();
        }
		
        System.out.println("SVDRecommender");
		Recommender svd = new SVDRecommender(dbmodel, new ALSWRFactorizer(dbmodel,3, 0.05f, 50));//itembase
		LongPrimitiveIterator iter2 = dbmodel.getUserIDs();
        while (iter2.hasNext()) {
            long uid = iter2.nextLong();
            List<RecommendedItem> list = svd.recommend(uid, 3);
            System.out.printf("uid:%s", uid);
            for (RecommendedItem ritem : list) {
                System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
            }
            System.out.println();
        }
        
        System.out.println("SVDRecommender_item");
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
        
        System.out.println("PearsonCorrelationSimilarity");
		ItemSimilarity similarity = new PearsonCorrelationSimilarity(dbmodel);// 计算内容相似度
		Recommender r3 = new GenericItemBasedRecommender(dbmodel, similarity);// 构造推荐引
		LongPrimitiveIterator iter3 = dbmodel.getUserIDs();
		while (iter3.hasNext()) {
			long uid = iter3.nextLong();
			List<RecommendedItem> list = r3.recommend(uid, 3);
			System.out.printf("uid:%s", uid);
			for (RecommendedItem ritem : list) {
				System.out.printf("(%s,%f)", ritem.getItemID(),
						ritem.getValue());
			}
			System.out.println();
		}
		
		System.out.println("CachingRecommender");
		Recommender r4 = new CachingRecommender(new SlopeOneRecommender(dbmodel));//构造推荐引擎
		LongPrimitiveIterator iter4 = dbmodel.getUserIDs();
		while (iter4.hasNext()) {
			long uid = iter4.nextLong();
			List<RecommendedItem> list = r4.recommend(uid, 3);
			System.out.printf("uid:%s", uid);
			for (RecommendedItem ritem : list) {
				System.out.printf("(%s,%f)", ritem.getItemID(),
						ritem.getValue());
			}
			System.out.println();
		}
	}
}
