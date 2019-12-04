package dev.elysion.fwa.converter;

import dev.elysion.fwa.dto.*;
import dev.elysion.fwa.entity.AdEntity;
import dev.elysion.fwa.entity.BasicConditionEntity;
import dev.elysion.fwa.entity.OfferEntity;
import dev.elysion.fwa.entity.TaskEntity;

import java.util.ArrayList;

public class RestConverter {

	public static Offer convert(OfferEntity source) {
		Offer target = new Offer();
		target.setId(source.getId());
		target.setTitle(source.getTitle());
		target.setDescription(source.getDescription());

		Image image = new Image();
		image.setImageUrl(source.getImageUrl());
		target.setImage(image);

		Category category = new Category();
		category.setId(source.getId());
		category.setName(source.getCategory()
							   .getName());
		target.setCategory(category);

		target.setContactPerson(Converter.convert(source.getContactPerson()));

		Organisation organisation = new Organisation();
		organisation.setName(source.getOrganisation()
								   .getOrgName());
		organisation.setId(source.getOrganisation()
								 .getId());
		target.setOrganisation(organisation);
		target.setActive(source.getActive());

		return target;
	}

	public static Ad convert(AdEntity source) {
		Ad target = new Ad();
		target.setId(source.getId());
		target.setTitle(source.getTitle());
		target.setNumberOfVolunteersNeeded(source.getNumberOfVolunteersNeeded());
		target.setReleaseDate(Converter.convertToLocalDateTime(source.getReleaseDate()));
		target.setValidUntil(Converter.convertToLocalDateTime(source.getValidUntil()));

		Image image = new Image();
		image.setImageUrl(source.getImageUrl());
		target.setImage(image);

		target.setBasicConditions(new ArrayList<BasicCondition>() {});
		for (BasicConditionEntity sourceBasicCondition : source.getBasicConditions()) {
			if (sourceBasicCondition.getDeleted() != null && !sourceBasicCondition.getDeleted()) {
				BasicCondition targetBasicCondition = new BasicCondition();
				targetBasicCondition.setDescription(sourceBasicCondition.getDescription());
				target.getBasicConditions()
					  .add(targetBasicCondition);
			}
		}

		target.setTasks(new ArrayList<Task>() {});
		for (TaskEntity sourceTask : source.getTasks()) {
			if (sourceTask.getDeleted() != null && !sourceTask.getDeleted()) {
				Task targetTask = new Task();
				targetTask.setDescription(sourceTask.getDescription());
				target.getTasks()
					  .add(targetTask);
			}
		}

		target.setOffer(RestConverter.convert(source.getOffer()));

		return target;
	}


}
