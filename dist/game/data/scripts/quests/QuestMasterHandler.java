/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests;

import java.util.logging.Level;
import java.util.logging.Logger;

import quests.Q00001_LettersOfLove.Q00001_LettersOfLove;
import quests.Q00002_WhatWomenWant.Q00002_WhatWomenWant;
import quests.Q00003_WillTheSealBeBroken.Q00003_WillTheSealBeBroken;
import quests.Q00004_LongLiveThePaagrioLord.Q00004_LongLiveThePaagrioLord;
import quests.Q00005_MinersFavor.Q00005_MinersFavor;
import quests.Q00006_StepIntoTheFuture.Q00006_StepIntoTheFuture;
import quests.Q00007_ATripBegins.Q00007_ATripBegins;
import quests.Q00008_AnAdventureBegins.Q00008_AnAdventureBegins;
import quests.Q00009_IntoTheCityOfHumans.Q00009_IntoTheCityOfHumans;
import quests.Q00010_IntoTheWorld.Q00010_IntoTheWorld;
import quests.Q00011_SecretMeetingWithKetraOrcs.Q00011_SecretMeetingWithKetraOrcs;
import quests.Q00012_SecretMeetingWithVarkaSilenos.Q00012_SecretMeetingWithVarkaSilenos;
import quests.Q00013_ParcelDelivery.Q00013_ParcelDelivery;
import quests.Q00014_WhereaboutsOfTheArchaeologist.Q00014_WhereaboutsOfTheArchaeologist;
import quests.Q00015_SweetWhispers.Q00015_SweetWhispers;
import quests.Q00016_TheComingDarkness.Q00016_TheComingDarkness;
import quests.Q00017_LightAndDarkness.Q00017_LightAndDarkness;
import quests.Q00018_MeetingWithTheGoldenRam.Q00018_MeetingWithTheGoldenRam;
import quests.Q00019_GoToThePastureland.Q00019_GoToThePastureland;
import quests.Q00020_BringUpWithLove.Q00020_BringUpWithLove;
import quests.Q00021_HiddenTruth.Q00021_HiddenTruth;
import quests.Q00022_TragedyInVonHellmannForest.Q00022_TragedyInVonHellmannForest;
import quests.Q00023_LidiasHeart.Q00023_LidiasHeart;
import quests.Q00024_InhabitantsOfTheForestOfTheDead.Q00024_InhabitantsOfTheForestOfTheDead;
import quests.Q00025_HidingBehindTheTruth.Q00025_HidingBehindTheTruth;
import quests.Q00027_ChestCaughtWithABaitOfWind.Q00027_ChestCaughtWithABaitOfWind;
import quests.Q00028_ChestCaughtWithABaitOfIcyAir.Q00028_ChestCaughtWithABaitOfIcyAir;
import quests.Q00029_ChestCaughtWithABaitOfEarth.Q00029_ChestCaughtWithABaitOfEarth;
import quests.Q00030_ChestCaughtWithABaitOfFire.Q00030_ChestCaughtWithABaitOfFire;
import quests.Q00031_SecretBuriedInTheSwamp.Q00031_SecretBuriedInTheSwamp;
import quests.Q00032_AnObviousLie.Q00032_AnObviousLie;
import quests.Q00033_MakeAPairOfDressShoes.Q00033_MakeAPairOfDressShoes;
import quests.Q00034_InSearchOfCloth.Q00034_InSearchOfCloth;
import quests.Q00035_FindGlitteringJewelry.Q00035_FindGlitteringJewelry;
import quests.Q00036_MakeASewingKit.Q00036_MakeASewingKit;
import quests.Q00037_MakeFormalWear.Q00037_MakeFormalWear;
import quests.Q00038_DragonFangs.Q00038_DragonFangs;
import quests.Q00039_RedEyedInvaders.Q00039_RedEyedInvaders;
import quests.Q00042_HelpTheUncle.Q00042_HelpTheUncle;
import quests.Q00043_HelpTheSister.Q00043_HelpTheSister;
import quests.Q00044_HelpTheSon.Q00044_HelpTheSon;
import quests.Q00045_ToTalkingIsland.Q00045_ToTalkingIsland;
import quests.Q00046_OnceMoreInTheArmsOfTheMotherTree.Q00046_OnceMoreInTheArmsOfTheMotherTree;
import quests.Q00047_IntoTheDarkElvenForest.Q00047_IntoTheDarkElvenForest;
import quests.Q00048_ToTheImmortalPlateau.Q00048_ToTheImmortalPlateau;
import quests.Q00049_TheRoadHome.Q00049_TheRoadHome;
import quests.Q00050_LanoscosSpecialBait.Q00050_LanoscosSpecialBait;
import quests.Q00051_OFullesSpecialBait.Q00051_OFullesSpecialBait;
import quests.Q00052_WilliesSpecialBait.Q00052_WilliesSpecialBait;
import quests.Q00053_LinnaeusSpecialBait.Q00053_LinnaeusSpecialBait;
import quests.Q00070_SagaOfThePhoenixKnight.Q00070_SagaOfThePhoenixKnight;
import quests.Q00071_SagaOfEvasTemplar.Q00071_SagaOfEvasTemplar;
import quests.Q00072_SagaOfTheSwordMuse.Q00072_SagaOfTheSwordMuse;
import quests.Q00073_SagaOfTheDuelist.Q00073_SagaOfTheDuelist;
import quests.Q00074_SagaOfTheDreadnought.Q00074_SagaOfTheDreadnought;
import quests.Q00075_SagaOfTheTitan.Q00075_SagaOfTheTitan;
import quests.Q00076_SagaOfTheGrandKhavatari.Q00076_SagaOfTheGrandKhavatari;
import quests.Q00077_SagaOfTheDominator.Q00077_SagaOfTheDominator;
import quests.Q00078_SagaOfTheDoomcryer.Q00078_SagaOfTheDoomcryer;
import quests.Q00079_SagaOfTheAdventurer.Q00079_SagaOfTheAdventurer;
import quests.Q00080_SagaOfTheWindRider.Q00080_SagaOfTheWindRider;
import quests.Q00081_SagaOfTheGhostHunter.Q00081_SagaOfTheGhostHunter;
import quests.Q00082_SagaOfTheSagittarius.Q00082_SagaOfTheSagittarius;
import quests.Q00083_SagaOfTheMoonlightSentinel.Q00083_SagaOfTheMoonlightSentinel;
import quests.Q00084_SagaOfTheGhostSentinel.Q00084_SagaOfTheGhostSentinel;
import quests.Q00085_SagaOfTheCardinal.Q00085_SagaOfTheCardinal;
import quests.Q00086_SagaOfTheHierophant.Q00086_SagaOfTheHierophant;
import quests.Q00087_SagaOfEvasSaint.Q00087_SagaOfEvasSaint;
import quests.Q00088_SagaOfTheArchmage.Q00088_SagaOfTheArchmage;
import quests.Q00089_SagaOfTheMysticMuse.Q00089_SagaOfTheMysticMuse;
import quests.Q00090_SagaOfTheStormScreamer.Q00090_SagaOfTheStormScreamer;
import quests.Q00091_SagaOfTheArcanaLord.Q00091_SagaOfTheArcanaLord;
import quests.Q00092_SagaOfTheElementalMaster.Q00092_SagaOfTheElementalMaster;
import quests.Q00093_SagaOfTheSpectralMaster.Q00093_SagaOfTheSpectralMaster;
import quests.Q00094_SagaOfTheSoultaker.Q00094_SagaOfTheSoultaker;
import quests.Q00095_SagaOfTheHellKnight.Q00095_SagaOfTheHellKnight;
import quests.Q00096_SagaOfTheSpectralDancer.Q00096_SagaOfTheSpectralDancer;
import quests.Q00097_SagaOfTheShillienTemplar.Q00097_SagaOfTheShillienTemplar;
import quests.Q00098_SagaOfTheShillienSaint.Q00098_SagaOfTheShillienSaint;
import quests.Q00099_SagaOfTheFortuneSeeker.Q00099_SagaOfTheFortuneSeeker;
import quests.Q00100_SagaOfTheMaestro.Q00100_SagaOfTheMaestro;
import quests.Q00101_SwordOfSolidarity.Q00101_SwordOfSolidarity;
import quests.Q00102_SeaOfSporesFever.Q00102_SeaOfSporesFever;
import quests.Q00103_SpiritOfCraftsman.Q00103_SpiritOfCraftsman;
import quests.Q00104_SpiritOfMirrors.Q00104_SpiritOfMirrors;
import quests.Q00105_SkirmishWithOrcs.Q00105_SkirmishWithOrcs;
import quests.Q00106_ForgottenTruth.Q00106_ForgottenTruth;
import quests.Q00107_MercilessPunishment.Q00107_MercilessPunishment;
import quests.Q00108_JumbleTumbleDiamondFuss.Q00108_JumbleTumbleDiamondFuss;
import quests.Q00109_InSearchOfTheNest.Q00109_InSearchOfTheNest;
import quests.Q00110_ToThePrimevalIsle.Q00110_ToThePrimevalIsle;
import quests.Q00111_ElrokianHuntersProof.Q00111_ElrokianHuntersProof;
import quests.Q00112_WalkOfFate.Q00112_WalkOfFate;
import quests.Q00113_StatusOfTheBeaconTower.Q00113_StatusOfTheBeaconTower;
import quests.Q00114_ResurrectionOfAnOldManager.Q00114_ResurrectionOfAnOldManager;
import quests.Q00115_TheOtherSideOfTruth.Q00115_TheOtherSideOfTruth;
import quests.Q00116_BeyondTheHillsOfWinter.Q00116_BeyondTheHillsOfWinter;
import quests.Q00117_TheOceanOfDistantStars.Q00117_TheOceanOfDistantStars;
import quests.Q00118_ToLeadAndBeLed.Q00118_ToLeadAndBeLed;
import quests.Q00119_LastImperialPrince.Q00119_LastImperialPrince;
import quests.Q00120_PavelsLastResearch.Q00120_PavelsLastResearch;
import quests.Q00121_PavelTheGiant.Q00121_PavelTheGiant;
import quests.Q00122_OminousNews.Q00122_OminousNews;
import quests.Q00123_TheLeaderAndTheFollower.Q00123_TheLeaderAndTheFollower;
import quests.Q00124_MeetingTheElroki.Q00124_MeetingTheElroki;
import quests.Q00125_TheNameOfEvil1.Q00125_TheNameOfEvil1;
import quests.Q00126_TheNameOfEvil2.Q00126_TheNameOfEvil2;
import quests.Q00127_KamaelAWindowToTheFuture.Q00127_KamaelAWindowToTheFuture;
// import quests.Q00127_KamaelAWindowToTheFuture.Q00127_KamaelAWindowToTheFuture;
import quests.Q00151_CureForFever.Q00151_CureForFever;
import quests.Q00152_ShardsOfGolem.Q00152_ShardsOfGolem;
import quests.Q00153_DeliverGoods.Q00153_DeliverGoods;
import quests.Q00154_SacrificeToTheSea.Q00154_SacrificeToTheSea;
import quests.Q00155_FindSirWindawood.Q00155_FindSirWindawood;
import quests.Q00156_MillenniumLove.Q00156_MillenniumLove;
import quests.Q00157_RecoverSmuggledGoods.Q00157_RecoverSmuggledGoods;
import quests.Q00158_SeedOfEvil.Q00158_SeedOfEvil;
import quests.Q00159_ProtectTheWaterSource.Q00159_ProtectTheWaterSource;
import quests.Q00160_NerupasRequest.Q00160_NerupasRequest;
import quests.Q00161_FruitOfTheMotherTree.Q00161_FruitOfTheMotherTree;
import quests.Q00162_CurseOfTheUndergroundFortress.Q00162_CurseOfTheUndergroundFortress;
import quests.Q00163_LegacyOfThePoet.Q00163_LegacyOfThePoet;
import quests.Q00164_BloodFiend.Q00164_BloodFiend;
import quests.Q00165_ShilensHunt.Q00165_ShilensHunt;
import quests.Q00166_MassOfDarkness.Q00166_MassOfDarkness;
import quests.Q00167_DwarvenKinship.Q00167_DwarvenKinship;
import quests.Q00168_DeliverSupplies.Q00168_DeliverSupplies;
import quests.Q00169_OffspringOfNightmares.Q00169_OffspringOfNightmares;
import quests.Q00170_DangerousSeduction.Q00170_DangerousSeduction;
import quests.Q00171_ActsOfEvil.Q00171_ActsOfEvil;
// import quests.Q00158_SeedOfEvil.Q00158_SeedOfEvil;
// import quests.Q00159_ProtectTheWaterSource.Q00159_ProtectTheWaterSource;
// import quests.Q00160_NerupasRequest.Q00160_NerupasRequest;
// import quests.Q00161_FruitOfTheMotherTree.Q00161_FruitOfTheMotherTree;
// import quests.Q00162_CurseOfTheUndergroundFortress.Q00162_CurseOfTheUndergroundFortress;
// import quests.Q00163_LegacyOfThePoet.Q00163_LegacyOfThePoet;
// import quests.Q00164_BloodFiend.Q00164_BloodFiend;
// import quests.Q00165_ShilensHunt.Q00165_ShilensHunt;
// import quests.Q00166_MassOfDarkness.Q00166_MassOfDarkness;
// import quests.Q00167_DwarvenKinship.Q00167_DwarvenKinship;
// import quests.Q00168_DeliverSupplies.Q00168_DeliverSupplies;
// import quests.Q00169_OffspringOfNightmares.Q00169_OffspringOfNightmares;
// import quests.Q00170_DangerousSeduction.Q00170_DangerousSeduction;
// import quests.Q00171_ActsOfEvil.Q00171_ActsOfEvil;
import quests.Q00211_TrialOfTheChallenger.Q00211_TrialOfTheChallenger;
import quests.Q00212_TrialOfDuty.Q00212_TrialOfDuty;
import quests.Q00213_TrialOfTheSeeker.Q00213_TrialOfTheSeeker;
import quests.Q00214_TrialOfTheScholar.Q00214_TrialOfTheScholar;
import quests.Q00215_TrialOfThePilgrim.Q00215_TrialOfThePilgrim;
import quests.Q00216_TrialOfTheGuildsman.Q00216_TrialOfTheGuildsman;
import quests.Q00217_TestimonyOfTrust.Q00217_TestimonyOfTrust;
import quests.Q00218_TestimonyOfLife.Q00218_TestimonyOfLife;
import quests.Q00219_TestimonyOfFate.Q00219_TestimonyOfFate;
import quests.Q00220_TestimonyOfGlory.Q00220_TestimonyOfGlory;
import quests.Q00221_TestimonyOfProsperity.Q00221_TestimonyOfProsperity;
import quests.Q00222_TestOfTheDuelist.Q00222_TestOfTheDuelist;
import quests.Q00223_TestOfTheChampion.Q00223_TestOfTheChampion;
import quests.Q00224_TestOfSagittarius.Q00224_TestOfSagittarius;
import quests.Q00225_TestOfTheSearcher.Q00225_TestOfTheSearcher;
import quests.Q00226_TestOfTheHealer.Q00226_TestOfTheHealer;
import quests.Q00227_TestOfTheReformer.Q00227_TestOfTheReformer;
import quests.Q00228_TestOfMagus.Q00228_TestOfMagus;
import quests.Q00229_TestOfWitchcraft.Q00229_TestOfWitchcraft;
import quests.Q00230_TestOfTheSummoner.Q00230_TestOfTheSummoner;
import quests.Q00231_TestOfTheMaestro.Q00231_TestOfTheMaestro;
import quests.Q00232_TestOfTheLord.Q00232_TestOfTheLord;
import quests.Q00233_TestOfTheWarSpirit.Q00233_TestOfTheWarSpirit;
import quests.Q00234_FatesWhisper.Q00234_FatesWhisper;
import quests.Q00235_MimirsElixir.Q00235_MimirsElixir;
import quests.Q00241_PossessorOfAPreciousSoul1.Q00241_PossessorOfAPreciousSoul1;
import quests.Q00242_PossessorOfAPreciousSoul2.Q00242_PossessorOfAPreciousSoul2;
import quests.Q00246_PossessorOfAPreciousSoul3.Q00246_PossessorOfAPreciousSoul3;
import quests.Q00247_PossessorOfAPreciousSoul4.Q00247_PossessorOfAPreciousSoul4;
import quests.Q00255_Tutorial.Q00255_Tutorial;
import quests.Q00257_TheGuardIsBusy.Q00257_TheGuardIsBusy;
import quests.Q00258_BringWolfPelts.Q00258_BringWolfPelts;
import quests.Q00259_RequestFromTheFarmOwner.Q00259_RequestFromTheFarmOwner;
import quests.Q00260_OrcHunting.Q00260_OrcHunting;
import quests.Q00261_CollectorsDream.Q00261_CollectorsDream;
import quests.Q00262_TradeWithTheIvoryTower.Q00262_TradeWithTheIvoryTower;
import quests.Q00263_OrcSubjugation.Q00263_OrcSubjugation;
import quests.Q00264_KeenClaws.Q00264_KeenClaws;
import quests.Q00265_BondsOfSlavery.Q00265_BondsOfSlavery;
import quests.Q00266_PleasOfPixies.Q00266_PleasOfPixies;
import quests.Q00267_WrathOfVerdure.Q00267_WrathOfVerdure;
import quests.Q00271_ProofOfValor.Q00271_ProofOfValor;
import quests.Q00272_WrathOfAncestors.Q00272_WrathOfAncestors;
import quests.Q00273_InvadersOfTheHolyLand.Q00273_InvadersOfTheHolyLand;
import quests.Q00274_SkirmishWithTheWerewolves.Q00274_SkirmishWithTheWerewolves;
import quests.Q00275_DarkWingedSpies.Q00275_DarkWingedSpies;
import quests.Q00276_TotemOfTheHestui.Q00276_TotemOfTheHestui;
import quests.Q00277_GatekeepersOffering.Q00277_GatekeepersOffering;
import quests.Q00291_RevengeOfTheRedbonnet.Q00291_RevengeOfTheRedbonnet;
// import quests.Q00291_RevengeOfTheRedbonnet.Q00291_RevengeOfTheRedbonnet;
import quests.Q00292_BrigandsSweep.Q00292_BrigandsSweep;
import quests.Q00293_TheHiddenVeins.Q00293_TheHiddenVeins;
import quests.Q00294_CovertBusiness.Q00294_CovertBusiness;
import quests.Q00295_DreamingOfTheSkies.Q00295_DreamingOfTheSkies;
import quests.Q00296_TarantulasSpiderSilk.Q00296_TarantulasSpiderSilk;
import quests.Q00297_GatekeepersFavor.Q00297_GatekeepersFavor;
import quests.Q00298_LizardmensConspiracy.Q00298_LizardmensConspiracy;
import quests.Q00299_GatherIngredientsForPie.Q00299_GatherIngredientsForPie;
// import quests.Q00298_LizardmensConspiracy.Q00298_LizardmensConspiracy;
// import quests.Q00299_GatherIngredientsForPie.Q00299_GatherIngredientsForPie;
import quests.Q00300_HuntingLetoLizardman.Q00300_HuntingLetoLizardman;
import quests.Q00303_CollectArrowheads.Q00303_CollectArrowheads;
import quests.Q00306_CrystalOfFireAndIce.Q00306_CrystalOfFireAndIce;
import quests.Q00313_CollectSpores.Q00313_CollectSpores;
import quests.Q00316_DestroyPlagueCarriers.Q00316_DestroyPlagueCarriers;
import quests.Q00317_CatchTheWind.Q00317_CatchTheWind;
import quests.Q00319_ScentOfDeath.Q00319_ScentOfDeath;
import quests.Q00320_BonesTellTheFuture.Q00320_BonesTellTheFuture;
import quests.Q00324_SweetestVenom.Q00324_SweetestVenom;
import quests.Q00325_GrimCollector.Q00325_GrimCollector;
import quests.Q00326_VanquishRemnants.Q00326_VanquishRemnants;
import quests.Q00327_RecoverTheFarmland.Q00327_RecoverTheFarmland;
import quests.Q00328_SenseForBusiness.Q00328_SenseForBusiness;
import quests.Q00329_CuriosityOfADwarf.Q00329_CuriosityOfADwarf;
import quests.Q00330_AdeptOfTaste.Q00330_AdeptOfTaste;
// import quests.Q00330_AdeptOfTaste.Q00330_AdeptOfTaste;
import quests.Q00331_ArrowOfVengeance.Q00331_ArrowOfVengeance;
import quests.Q00333_HuntOfTheBlackLion.Q00333_HuntOfTheBlackLion;
import quests.Q00334_TheWishingPotion.Q00334_TheWishingPotion;
import quests.Q00335_TheSongOfTheHunter.Q00335_TheSongOfTheHunter;
// import quests.Q00334_TheWishingPotion.Q00334_TheWishingPotion;
// import quests.Q00335_TheSongOfTheHunter.Q00335_TheSongOfTheHunter;
import quests.Q00336_CoinsOfMagic.Q00336_CoinsOfMagic;
import quests.Q00337_AudienceWithTheLandDragon.Q00337_AudienceWithTheLandDragon;
import quests.Q00338_AlligatorHunter.Q00338_AlligatorHunter;
import quests.Q00340_SubjugationOfLizardmen.Q00340_SubjugationOfLizardmen;
import quests.Q00341_HuntingForWildBeasts.Q00341_HuntingForWildBeasts;
import quests.Q00343_UnderTheShadowOfTheIvoryTower.Q00343_UnderTheShadowOfTheIvoryTower;
// import quests.Q00338_AlligatorHunter.Q00338_AlligatorHunter;
// import quests.Q00340_SubjugationOfLizardmen.Q00340_SubjugationOfLizardmen;
// import quests.Q00341_HuntingForWildBeasts.Q00341_HuntingForWildBeasts;
// import quests.Q00343_UnderTheShadowOfTheIvoryTower.Q00343_UnderTheShadowOfTheIvoryTower;
import quests.Q00344_1000YearsTheEndOfLamentation.Q00344_1000YearsTheEndOfLamentation;
import quests.Q00345_MethodToRaiseTheDead.Q00345_MethodToRaiseTheDead;
import quests.Q00347_GoGetTheCalculator.Q00347_GoGetTheCalculator;
// import quests.Q00345_MethodToRaiseTheDead.Q00345_MethodToRaiseTheDead;
// import quests.Q00347_GoGetTheCalculator.Q00347_GoGetTheCalculator;
import quests.Q00348_AnArrogantSearch.Q00348_AnArrogantSearch;
import quests.Q00350_EnhanceYourWeapon.Q00350_EnhanceYourWeapon;
import quests.Q00351_BlackSwan.Q00351_BlackSwan;
import quests.Q00352_HelpRoodRaiseANewPet.Q00352_HelpRoodRaiseANewPet;
import quests.Q00353_PowerOfDarkness.Q00353_PowerOfDarkness;
// import quests.Q00351_BlackSwan.Q00351_BlackSwan;
// import quests.Q00352_HelpRoodRaiseANewPet.Q00352_HelpRoodRaiseANewPet;
// import quests.Q00353_PowerOfDarkness.Q00353_PowerOfDarkness;
import quests.Q00354_ConquestOfAlligatorIsland.Q00354_ConquestOfAlligatorIsland;
import quests.Q00355_FamilyHonor.Q00355_FamilyHonor;
import quests.Q00356_DigUpTheSeaOfSpores.Q00356_DigUpTheSeaOfSpores;
import quests.Q00357_WarehouseKeepersAmbition.Q00357_WarehouseKeepersAmbition;
// import quests.Q00357_WarehouseKeepersAmbition.Q00357_WarehouseKeepersAmbition;
import quests.Q00358_IllegitimateChildOfTheGoddess.Q00358_IllegitimateChildOfTheGoddess;
import quests.Q00359_ForASleeplessDeadman.Q00359_ForASleeplessDeadman;
import quests.Q00360_PlunderTheirSupplies.Q00360_PlunderTheirSupplies;
import quests.Q00362_BardsMandolin.Q00362_BardsMandolin;
import quests.Q00363_SorrowfulSoundOfFlute.Q00363_SorrowfulSoundOfFlute;
import quests.Q00364_JovialAccordion.Q00364_JovialAccordion;
import quests.Q00365_DevilsLegacy.Q00365_DevilsLegacy;
import quests.Q00366_SilverHairedShaman.Q00366_SilverHairedShaman;
import quests.Q00367_ElectrifyingRecharge.Q00367_ElectrifyingRecharge;
import quests.Q00368_TrespassingIntoTheHolyGround.Q00368_TrespassingIntoTheHolyGround;
// import quests.Q00362_BardsMandolin.Q00362_BardsMandolin;
// import quests.Q00363_SorrowfulSoundOfFlute.Q00363_SorrowfulSoundOfFlute;
// import quests.Q00364_JovialAccordion.Q00364_JovialAccordion;
// import quests.Q00365_DevilsLegacy.Q00365_DevilsLegacy;
// import quests.Q00366_SilverHairedShaman.Q00366_SilverHairedShaman;
// import quests.Q00367_ElectrifyingRecharge.Q00367_ElectrifyingRecharge;
// import quests.Q00368_TrespassingIntoTheHolyGround.Q00368_TrespassingIntoTheHolyGround;
import quests.Q00369_CollectorOfJewels.Q00369_CollectorOfJewels;
import quests.Q00370_AnElderSowsSeeds.Q00370_AnElderSowsSeeds;
import quests.Q00371_ShrieksOfGhosts.Q00371_ShrieksOfGhosts;
// import quests.Q00371_ShrieksOfGhosts.Q00371_ShrieksOfGhosts;
import quests.Q00372_LegacyOfInsolence.Q00372_LegacyOfInsolence;
import quests.Q00373_SupplierOfReagents.Q00373_SupplierOfReagents;
import quests.Q00374_WhisperOfDreamsPart1.Q00374_WhisperOfDreamsPart1;
import quests.Q00375_WhisperOfDreamsPart2.Q00375_WhisperOfDreamsPart2;
import quests.Q00376_ExplorationOfTheGiantsCavePart1.Q00376_ExplorationOfTheGiantsCavePart1;
import quests.Q00377_ExplorationOfTheGiantsCavePart2.Q00377_ExplorationOfTheGiantsCavePart2;
import quests.Q00378_GrandFeast.Q00378_GrandFeast;
import quests.Q00379_FantasyWine.Q00379_FantasyWine;
import quests.Q00380_BringOutTheFlavorOfIngredients.Q00380_BringOutTheFlavorOfIngredients;
// import quests.Q00378_GrandFeast.Q00378_GrandFeast;
// import quests.Q00379_FantasyWine.Q00379_FantasyWine;
// import quests.Q00380_BringOutTheFlavorOfIngredients.Q00380_BringOutTheFlavorOfIngredients;
import quests.Q00381_LetsBecomeARoyalMember.Q00381_LetsBecomeARoyalMember;
import quests.Q00382_KailsMagicCoin.Q00382_KailsMagicCoin;
import quests.Q00383_TreasureHunt.Q00383_TreasureHunt;
import quests.Q00384_WarehouseKeepersPastime.Q00384_WarehouseKeepersPastime;
import quests.Q00385_YokeOfThePast.Q00385_YokeOfThePast;
// import quests.Q00383_TreasureHunt.Q00383_TreasureHunt;
// import quests.Q00384_WarehouseKeepersPastime.Q00384_WarehouseKeepersPastime;
// import quests.Q00385_YokeOfThePast.Q00385_YokeOfThePast;
import quests.Q00386_StolenDignity.Q00386_StolenDignity;
import quests.Q00401_PathOfTheWarrior.Q00401_PathOfTheWarrior;
import quests.Q00402_PathOfTheHumanKnight.Q00402_PathOfTheHumanKnight;
import quests.Q00403_PathOfTheRogue.Q00403_PathOfTheRogue;
import quests.Q00404_PathOfTheHumanWizard.Q00404_PathOfTheHumanWizard;
import quests.Q00405_PathOfTheCleric.Q00405_PathOfTheCleric;
import quests.Q00406_PathOfTheElvenKnight.Q00406_PathOfTheElvenKnight;
import quests.Q00407_PathOfTheElvenScout.Q00407_PathOfTheElvenScout;
import quests.Q00408_PathOfTheElvenWizard.Q00408_PathOfTheElvenWizard;
import quests.Q00409_PathOfTheElvenOracle.Q00409_PathOfTheElvenOracle;
import quests.Q00410_PathOfThePalusKnight.Q00410_PathOfThePalusKnight;
import quests.Q00411_PathOfTheAssassin.Q00411_PathOfTheAssassin;
import quests.Q00412_PathOfTheDarkWizard.Q00412_PathOfTheDarkWizard;
import quests.Q00413_PathOfTheShillienOracle.Q00413_PathOfTheShillienOracle;
import quests.Q00414_PathOfTheOrcRaider.Q00414_PathOfTheOrcRaider;
import quests.Q00415_PathOfTheOrcMonk.Q00415_PathOfTheOrcMonk;
import quests.Q00416_PathOfTheOrcShaman.Q00416_PathOfTheOrcShaman;
import quests.Q00417_PathOfTheScavenger.Q00417_PathOfTheScavenger;
import quests.Q00418_PathOfTheArtisan.Q00418_PathOfTheArtisan;
import quests.Q00419_GetAPet.Q00419_GetAPet;
// import quests.Q00419_GetAPet.Q00419_GetAPet;
import quests.Q00420_LittleWing.Q00420_LittleWing;
import quests.Q00421_LittleWingsBigAdventure.Q00421_LittleWingsBigAdventure;
import quests.Q00422_RepentYourSins.Q00422_RepentYourSins;
import quests.Q00426_QuestForFishingShot.Q00426_QuestForFishingShot;
// import quests.Q00422_RepentYourSins.Q00422_RepentYourSins;
// import quests.Q00426_QuestForFishingShot.Q00426_QuestForFishingShot;
import quests.Q00431_WeddingMarch.Q00431_WeddingMarch;
import quests.Q00432_BirthdayPartySong.Q00432_BirthdayPartySong;
import quests.Q00501_ProofOfClanAlliance.Q00501_ProofOfClanAlliance;
import quests.Q00503_PursuitOfClanAmbition.Q00503_PursuitOfClanAmbition;
// import quests.Q00504_CompetitionForTheBanditStronghold.Q00504_CompetitionForTheBanditStronghold;
import quests.Q00505_BloodOffering.Q00505_BloodOffering;
import quests.Q00508_AClansReputation.Q00508_AClansReputation;
import quests.Q00509_AClansFame.Q00509_AClansFame;
import quests.Q00510_AClansPrestige.Q00510_AClansPrestige;
import quests.Q00601_WatchingEyes.Q00601_WatchingEyes;
import quests.Q00602_ShadowOfLight.Q00602_ShadowOfLight;
import quests.Q00603_DaimonTheWhiteEyedPart1.Q00603_DaimonTheWhiteEyedPart1;
import quests.Q00604_DaimonTheWhiteEyedPart2.Q00604_DaimonTheWhiteEyedPart2;
// import quests.Q00508_AClansReputation.Q00508_AClansReputation;
// import quests.Q00509_AClansFame.Q00509_AClansFame;
// import quests.Q00510_AClansPrestige.Q00510_AClansPrestige;
// import quests.Q00601_WatchingEyes.Q00601_WatchingEyes;
// import quests.Q00602_ShadowOfLight.Q00602_ShadowOfLight;
// import quests.Q00603_DaimonTheWhiteEyedPart1.Q00603_DaimonTheWhiteEyedPart1;
// import quests.Q00604_DaimonTheWhiteEyedPart2.Q00604_DaimonTheWhiteEyedPart2;
import quests.Q00605_AllianceWithKetraOrcs.Q00605_AllianceWithKetraOrcs;
import quests.Q00606_BattleAgainstVarkaSilenos.Q00606_BattleAgainstVarkaSilenos;
import quests.Q00607_ProveYourCourageKetra.Q00607_ProveYourCourageKetra;
import quests.Q00608_SlayTheEnemyCommanderKetra.Q00608_SlayTheEnemyCommanderKetra;
import quests.Q00609_MagicalPowerOfWaterPart1.Q00609_MagicalPowerOfWaterPart1;
import quests.Q00610_MagicalPowerOfWaterPart2.Q00610_MagicalPowerOfWaterPart2;
import quests.Q00611_AllianceWithVarkaSilenos.Q00611_AllianceWithVarkaSilenos;
import quests.Q00612_BattleAgainstKetraOrcs.Q00612_BattleAgainstKetraOrcs;
import quests.Q00613_ProveYourCourageVarka.Q00613_ProveYourCourageVarka;
import quests.Q00614_SlayTheEnemyCommanderVarka.Q00614_SlayTheEnemyCommanderVarka;
import quests.Q00615_MagicalPowerOfFirePart1.Q00615_MagicalPowerOfFirePart1;
import quests.Q00616_MagicalPowerOfFirePart2.Q00616_MagicalPowerOfFirePart2;
import quests.Q00617_GatherTheFlames.Q00617_GatherTheFlames;
import quests.Q00618_IntoTheFlame.Q00618_IntoTheFlame;
import quests.Q00619_RelicsOfTheOldEmpire.Q00619_RelicsOfTheOldEmpire;
import quests.Q00620_FourGoblets.Q00620_FourGoblets;
import quests.Q00621_EggDelivery.Q00621_EggDelivery;
// import quests.Q00621_EggDelivery.Q00621_EggDelivery;
import quests.Q00622_SpecialtyLiquorDelivery.Q00622_SpecialtyLiquorDelivery;
import quests.Q00623_TheFinestFood.Q00623_TheFinestFood;
import quests.Q00624_TheFinestIngredientsPart1.Q00624_TheFinestIngredientsPart1;
import quests.Q00625_TheFinestIngredientsPart2.Q00625_TheFinestIngredientsPart2;
import quests.Q00626_ADarkTwilight.Q00626_ADarkTwilight;
import quests.Q00627_HeartInSearchOfPower.Q00627_HeartInSearchOfPower;
// import quests.Q00624_TheFinestIngredientsPart1.Q00624_TheFinestIngredientsPart1;
// import quests.Q00625_TheFinestIngredientsPart2.Q00625_TheFinestIngredientsPart2;
// import quests.Q00626_ADarkTwilight.Q00626_ADarkTwilight;
// import quests.Q00627_HeartInSearchOfPower.Q00627_HeartInSearchOfPower;
import quests.Q00628_HuntGoldenRam.Q00628_HuntGoldenRam;
import quests.Q00629_CleanUpTheSwampOfScreams.Q00629_CleanUpTheSwampOfScreams;
import quests.Q00631_DeliciousTopChoiceMeat.Q00631_DeliciousTopChoiceMeat;
import quests.Q00632_NecromancersRequest.Q00632_NecromancersRequest;
import quests.Q00633_InTheForgottenVillage.Q00633_InTheForgottenVillage;
// import quests.Q00629_CleanUpTheSwampOfScreams.Q00629_CleanUpTheSwampOfScreams;
// import quests.Q00631_DeliciousTopChoiceMeat.Q00631_DeliciousTopChoiceMeat;
// import quests.Q00632_NecromancersRequest.Q00632_NecromancersRequest;
// import quests.Q00633_InTheForgottenVillage.Q00633_InTheForgottenVillage;
import quests.Q00634_InSearchOfFragmentsOfDimension.Q00634_InSearchOfFragmentsOfDimension;
import quests.Q00635_IntoTheDimensionalRift.Q00635_IntoTheDimensionalRift;
import quests.Q00636_TruthBeyond.Q00636_TruthBeyond;
import quests.Q00637_ThroughOnceMore.Q00637_ThroughOnceMore;
// import quests.Q00637_ThroughOnceMore.Q00637_ThroughOnceMore;
import quests.Q00638_SeekersOfTheHolyGrail.Q00638_SeekersOfTheHolyGrail;
import quests.Q00639_GuardiansOfTheHolyGrail.Q00639_GuardiansOfTheHolyGrail;
import quests.Q00640_TheZeroHour.Q00640_TheZeroHour;
import quests.Q00641_AttackSailren.Q00641_AttackSailren;
import quests.Q00642_APowerfulPrimevalCreature.Q00642_APowerfulPrimevalCreature;
import quests.Q00643_RiseAndFallOfTheElrokiTribe.Q00643_RiseAndFallOfTheElrokiTribe;
import quests.Q00644_GraveRobberAnnihilation.Q00644_GraveRobberAnnihilation;
import quests.Q00645_GhostsOfBatur.Q00645_GhostsOfBatur;
import quests.Q00660_AidingTheFloranVillage.Q00660_AidingTheFloranVillage;
import quests.Q00661_MakingTheHarvestGroundsSafe.Q00661_MakingTheHarvestGroundsSafe;
// import quests.Q00644_GraveRobberAnnihilation.Q00644_GraveRobberAnnihilation;
// import quests.Q00645_GhostsOfBatur.Q00645_GhostsOfBatur;
// import quests.Q00646_SignsOfRevolt.Q00646_SignsOfRevolt;
// import quests.Q00647_InfluxOfMachines.Q00647_InfluxOfMachines;
// import quests.Q00648_AnIceMerchantsDream.Q00648_AnIceMerchantsDream;
// import quests.Q00649_ALooterAndARailroadMan.Q00649_ALooterAndARailroadMan;
// import quests.Q00650_ABrokenDream.Q00650_ABrokenDream;
// import quests.Q00651_RunawayYouth.Q00651_RunawayYouth;
// import quests.Q00652_AnAgedExAdventurer.Q00652_AnAgedExAdventurer;
// import quests.Q00653_WildMaiden.Q00653_WildMaiden;
// import quests.Q00654_JourneyToASettlement.Q00654_JourneyToASettlement;
// import quests.Q00655_AGrandPlanForTamingWildBeasts.Q00655_AGrandPlanForTamingWildBeasts;
// import quests.Q00659_IdRatherBeCollectingFairyBreath.Q00659_IdRatherBeCollectingFairyBreath;
// import quests.Q00660_AidingTheFloranVillage.Q00660_AidingTheFloranVillage;
// import quests.Q00661_MakingTheHarvestGroundsSafe.Q00661_MakingTheHarvestGroundsSafe;
import quests.Q00662_AGameOfCards.Q00662_AGameOfCards;
import quests.Q00663_SeductiveWhispers.Q00663_SeductiveWhispers;
// import quests.Q00663_SeductiveWhispers.Q00663_SeductiveWhispers;
import quests.Q00688_DefeatTheElrokianRaiders.Q00688_DefeatTheElrokianRaiders;

/**
 * @author NosBit
 */
public class QuestMasterHandler
{
	private static final Logger LOGGER = Logger.getLogger(QuestMasterHandler.class.getName());
	
	private static final Class<?>[] QUESTS =
	{
		Q00001_LettersOfLove.class, // Added, need html work and test.
		Q00002_WhatWomenWant.class, // Added, need html work and test.
		Q00003_WillTheSealBeBroken.class, // Added, need html work and test.
		Q00004_LongLiveThePaagrioLord.class, // Added, need html work and test.
		Q00005_MinersFavor.class, // Added, need html work and test.
		Q00006_StepIntoTheFuture.class, // Added, need html work and test.
		Q00007_ATripBegins.class, // Added, need html work and test.
		Q00008_AnAdventureBegins.class, // Added, need html work and test.
		Q00009_IntoTheCityOfHumans.class, // Added, need html work and test.
		Q00010_IntoTheWorld.class, // Added, need html work and test.
		Q00011_SecretMeetingWithKetraOrcs.class, // Added, need html work and test.
		Q00012_SecretMeetingWithVarkaSilenos.class, // Added, need html work and test.
		Q00013_ParcelDelivery.class, // Added, need html work and test.
		Q00014_WhereaboutsOfTheArchaeologist.class, // Added, need html work and test.
		Q00015_SweetWhispers.class, // Added, need html work and test.
		Q00016_TheComingDarkness.class, // Added, need html work and test.
		Q00017_LightAndDarkness.class, // Added, need html work and test.
		Q00018_MeetingWithTheGoldenRam.class, // Added, need html work and test.
		Q00019_GoToThePastureland.class, // Added, need html work and test.
		Q00020_BringUpWithLove.class, // Added, need html work and test.
		Q00021_HiddenTruth.class, // Added, need html work and test. // Added, need html work and test.
		Q00022_TragedyInVonHellmannForest.class,
		Q00023_LidiasHeart.class, // Added, need html work and test.
		Q00024_InhabitantsOfTheForestOfTheDead.class, // Added, need html work and test.
		Q00025_HidingBehindTheTruth.class, // Added, need html work and test.
		Q00027_ChestCaughtWithABaitOfWind.class, // Added, need html work and test.
		Q00028_ChestCaughtWithABaitOfIcyAir.class, // Added, need html work and test.
		Q00029_ChestCaughtWithABaitOfEarth.class, // Added, need html work and test.
		Q00030_ChestCaughtWithABaitOfFire.class, // Added, need html work and test.
		Q00031_SecretBuriedInTheSwamp.class, // Added, need html work and test.
		Q00032_AnObviousLie.class, // Added, need html work and test.
		Q00033_MakeAPairOfDressShoes.class, // Added, need html work and test.
		Q00034_InSearchOfCloth.class, // Added, need html work and test.
		Q00035_FindGlitteringJewelry.class, // Added, need html work and test.
		Q00036_MakeASewingKit.class, // Added, need html work and test.
		Q00037_MakeFormalWear.class, // Added, need html work and test.
		Q00038_DragonFangs.class, // Added, need html work and test.
		Q00039_RedEyedInvaders.class, // Added, need html work and test.
		Q00042_HelpTheUncle.class, // Added, need html work and test.
		Q00043_HelpTheSister.class, // Added, need html work and test.
		Q00044_HelpTheSon.class, // Added, need html work and test.
		Q00045_ToTalkingIsland.class, // Added, need html work and test.
		Q00046_OnceMoreInTheArmsOfTheMotherTree.class, // Added, need html work and test.
		Q00047_IntoTheDarkElvenForest.class, // Added, need html work and test.
		Q00048_ToTheImmortalPlateau.class, // Added, need html work and test.
		Q00049_TheRoadHome.class, // Added, need html work and test.
		Q00050_LanoscosSpecialBait.class, // Added, need html work and test.
		Q00051_OFullesSpecialBait.class, // Added, need html work and test.
		Q00052_WilliesSpecialBait.class, // Added, need html work and test.
		Q00053_LinnaeusSpecialBait.class, // Added, need html work and test.
		Q00070_SagaOfThePhoenixKnight.class,
		Q00071_SagaOfEvasTemplar.class,
		Q00072_SagaOfTheSwordMuse.class,
		Q00073_SagaOfTheDuelist.class,
		Q00074_SagaOfTheDreadnought.class,
		Q00075_SagaOfTheTitan.class,
		Q00076_SagaOfTheGrandKhavatari.class,
		Q00077_SagaOfTheDominator.class,
		Q00078_SagaOfTheDoomcryer.class,
		Q00079_SagaOfTheAdventurer.class,
		Q00080_SagaOfTheWindRider.class,
		Q00081_SagaOfTheGhostHunter.class,
		Q00082_SagaOfTheSagittarius.class,
		Q00083_SagaOfTheMoonlightSentinel.class,
		Q00084_SagaOfTheGhostSentinel.class,
		Q00085_SagaOfTheCardinal.class,
		Q00086_SagaOfTheHierophant.class,
		Q00087_SagaOfEvasSaint.class,
		Q00088_SagaOfTheArchmage.class,
		Q00089_SagaOfTheMysticMuse.class,
		Q00090_SagaOfTheStormScreamer.class,
		Q00091_SagaOfTheArcanaLord.class,
		Q00092_SagaOfTheElementalMaster.class,
		Q00093_SagaOfTheSpectralMaster.class,
		Q00094_SagaOfTheSoultaker.class,
		Q00095_SagaOfTheHellKnight.class,
		Q00096_SagaOfTheSpectralDancer.class,
		Q00097_SagaOfTheShillienTemplar.class,
		Q00098_SagaOfTheShillienSaint.class,
		Q00099_SagaOfTheFortuneSeeker.class,
		Q00100_SagaOfTheMaestro.class,
		Q00101_SwordOfSolidarity.class, // Redone, need update html and test.
		Q00102_SeaOfSporesFever.class,
		Q00103_SpiritOfCraftsman.class, // Redone, need update html and test.
		Q00104_SpiritOfMirrors.class, // Redone, need update html and test.
		Q00105_SkirmishWithOrcs.class, // Redone, need update html and test.
		Q00106_ForgottenTruth.class, // Redone, need update html and test.
		Q00107_MercilessPunishment.class, // Redone, need update html and test.
		Q00108_JumbleTumbleDiamondFuss.class, // Redone, need update html and test.
		Q00109_InSearchOfTheNest.class, // Added, need html work and test.
		Q00110_ToThePrimevalIsle.class, // Added, need html work and test.
		Q00111_ElrokianHuntersProof.class, // Added, need html work and test.
		Q00112_WalkOfFate.class, // Added, need html work and test.
		Q00113_StatusOfTheBeaconTower.class, // Added, need html work and test.
		Q00114_ResurrectionOfAnOldManager.class, // Added, need html work and test.
		Q00115_TheOtherSideOfTruth.class, // Added, need html work and test.
		Q00116_BeyondTheHillsOfWinter.class, // Added, need html work and test.
		Q00117_TheOceanOfDistantStars.class, // Added, need html work and test.
		Q00118_ToLeadAndBeLed.class, // Added, need html work and test.
		Q00119_LastImperialPrince.class, // Added, need html work and test.
		Q00120_PavelsLastResearch.class, // Added, need html work and test.
		Q00121_PavelTheGiant.class, // Added, need html work and test.
		Q00122_OminousNews.class, // Added, need html work and test.
		Q00123_TheLeaderAndTheFollower.class, // Added, need html work and test.
		Q00124_MeetingTheElroki.class, // Added, need html work and test.
		Q00125_TheNameOfEvil1.class, // Added, need html work and test.
		Q00126_TheNameOfEvil2.class, // Added, need html work and test.
		Q00127_KamaelAWindowToTheFuture.class, // Added, need html work and test.
		Q00151_CureForFever.class, // Added, need html work and test.
		Q00152_ShardsOfGolem.class, // Added, need html work and test.
		Q00153_DeliverGoods.class, // Added, need html work and test.
		Q00154_SacrificeToTheSea.class, // Added, need html work and test.
		Q00155_FindSirWindawood.class, // Added, need html work and test.
		Q00156_MillenniumLove.class, // Added, need html work and test.
		Q00157_RecoverSmuggledGoods.class, // Added, need html work and test.
		Q00158_SeedOfEvil.class, // Added, need html work and test.
		Q00159_ProtectTheWaterSource.class, // Added, need html work and test.
		Q00160_NerupasRequest.class, // Added, need html work and test.
		Q00161_FruitOfTheMotherTree.class, // Added, need html work and test.
		Q00162_CurseOfTheUndergroundFortress.class, // Added, need html work and test.
		Q00163_LegacyOfThePoet.class, // Added, need html work and test.
		Q00164_BloodFiend.class, // Added, need html work and test.
		Q00165_ShilensHunt.class, // Added, need html work and test.
		Q00166_MassOfDarkness.class, // Added, need html work and test.
		Q00167_DwarvenKinship.class, // Added, need html work and test.
		Q00168_DeliverSupplies.class, // Added, need html work and test.
		Q00169_OffspringOfNightmares.class, // Added, need html work and test.
		Q00170_DangerousSeduction.class, // Added, need html work and test.
		Q00171_ActsOfEvil.class, // Added, need html work and test.
		Q00211_TrialOfTheChallenger.class, // Added, need html work and test.
		Q00212_TrialOfDuty.class, // Added, need html work and test.
		Q00213_TrialOfTheSeeker.class, // Added, need html work and test.
		Q00214_TrialOfTheScholar.class, // Added, need html work and test.
		Q00215_TrialOfThePilgrim.class, // Added, need html work and test.
		Q00216_TrialOfTheGuildsman.class, // Added, need html work and test.
		Q00217_TestimonyOfTrust.class, // Added, need html work and test.
		Q00218_TestimonyOfLife.class, // Added, need html work and test.
		Q00219_TestimonyOfFate.class, // Added, need html work and test.
		Q00220_TestimonyOfGlory.class, // Added, need html work and test.
		Q00221_TestimonyOfProsperity.class, // Added, need html work and test.
		Q00222_TestOfTheDuelist.class, // Added, need html work and test.
		Q00223_TestOfTheChampion.class, // Added, need html work and test.
		Q00224_TestOfSagittarius.class, // Added, need html work and test.
		Q00225_TestOfTheSearcher.class, // Added, need html work and test.
		Q00226_TestOfTheHealer.class, // Added, need html work and test.
		Q00227_TestOfTheReformer.class, // Added, need html work and test.
		Q00228_TestOfMagus.class, // Added, need html work and test.
		Q00229_TestOfWitchcraft.class, // Added, need html work and test.
		Q00230_TestOfTheSummoner.class, // Added, need html work and test.
		Q00231_TestOfTheMaestro.class, // Added, need html work and test.
		Q00232_TestOfTheLord.class, // Added, need html work and test.
		Q00233_TestOfTheWarSpirit.class, // Added, need html work and test.
		Q00234_FatesWhisper.class, // Added, need html work and test.
		Q00235_MimirsElixir.class, // Added, need html work and test.
		Q00241_PossessorOfAPreciousSoul1.class, // Added, need html work and test.
		Q00242_PossessorOfAPreciousSoul2.class, // Added, need html work and test.
		Q00246_PossessorOfAPreciousSoul3.class, // Added, need html work and test.
		Q00247_PossessorOfAPreciousSoul4.class, // Added, need html work and test.
		Q00255_Tutorial.class,
		Q00257_TheGuardIsBusy.class, // Redone, need update html and test.
		Q00258_BringWolfPelts.class, // Added, need html work and test.
		Q00259_RequestFromTheFarmOwner.class, // Added, need html work and test.
		Q00260_OrcHunting.class, // Redone, need update html and test.
		Q00261_CollectorsDream.class, // Added, need html work and test.
		Q00262_TradeWithTheIvoryTower.class, // Added, need html work and test.
		Q00263_OrcSubjugation.class, // Added, need html work and test.
		Q00264_KeenClaws.class, // Added, need html work and test.
		Q00265_BondsOfSlavery.class, // Redone, need update html and test.
		Q00266_PleasOfPixies.class, // Added, need html work and test.
		Q00267_WrathOfVerdure.class, // Added, need html work and test.
		Q00271_ProofOfValor.class, // Added, need html work and test.
		Q00272_WrathOfAncestors.class, // Added, need html work and test.
		Q00273_InvadersOfTheHolyLand.class, // Redone, need update html and test.
		Q00274_SkirmishWithTheWerewolves.class, // Added, need html work and test.
		Q00275_DarkWingedSpies.class, // Added, need html work and test.
		Q00276_TotemOfTheHestui.class, // Added, need html work and test.
		Q00277_GatekeepersOffering.class, // Added, need html work and test.
		Q00291_RevengeOfTheRedbonnet.class, // Added, need html work and test.
		Q00292_BrigandsSweep.class, // Added, need html work and test.
		Q00293_TheHiddenVeins.class, // Redone, need update html and test.
		Q00294_CovertBusiness.class, // Added, need html work and test.
		Q00295_DreamingOfTheSkies.class, // Added, need html work and test.
		Q00296_TarantulasSpiderSilk.class, // Added, need html work and test.
		Q00297_GatekeepersFavor.class, // Added, need html work and test.
		Q00298_LizardmensConspiracy.class, // Added, need html work and test.
		Q00299_GatherIngredientsForPie.class, // Added, need html work and test.
		Q00300_HuntingLetoLizardman.class, // Added, need html work and test.
		Q00303_CollectArrowheads.class, // Added, need html work and test.
		Q00306_CrystalOfFireAndIce.class, // Added, need html work and test.
		Q00313_CollectSpores.class, // Added, need html work and test.
		Q00316_DestroyPlagueCarriers.class, // Added, need html work and test.
		Q00317_CatchTheWind.class, // Added, need html work and test.
		Q00319_ScentOfDeath.class, // Added, need html work and test.
		Q00320_BonesTellTheFuture.class, // Added, need html work and test.
		Q00324_SweetestVenom.class, // Added, need html work and test.
		Q00325_GrimCollector.class, // Added, need html work and test.
		Q00326_VanquishRemnants.class, // Added, need html work and test.
		Q00327_RecoverTheFarmland.class, // Added, need html work and test.
		Q00328_SenseForBusiness.class, // Added, need html work and test.
		Q00329_CuriosityOfADwarf.class, // Added, need html work and test.
		Q00330_AdeptOfTaste.class, // Added, need html work and test.
		Q00331_ArrowOfVengeance.class, // Added, need html work and test.
		Q00333_HuntOfTheBlackLion.class, // Added, need html work and test.
		Q00334_TheWishingPotion.class, // Added, need html work and test.
		Q00335_TheSongOfTheHunter.class, // Added, need html work and test.
		Q00336_CoinsOfMagic.class, // Added, need html work and test.
		Q00337_AudienceWithTheLandDragon.class, // Added, need html work and test.
		Q00338_AlligatorHunter.class, // Added, need html work and test.
		Q00340_SubjugationOfLizardmen.class, // Added, need html work and test.
		Q00341_HuntingForWildBeasts.class, // Added, need html work and test.
		Q00343_UnderTheShadowOfTheIvoryTower.class, // Added, need html work and test.
		Q00344_1000YearsTheEndOfLamentation.class, // Added, need html work and test.
		Q00345_MethodToRaiseTheDead.class, // Added, need html work and test.
		Q00347_GoGetTheCalculator.class, // Added, need html work and test.
		Q00348_AnArrogantSearch.class, // Added, need html work and test.
		Q00350_EnhanceYourWeapon.class, // Added, need html work and test.
		Q00351_BlackSwan.class, // Added, need html work and test.
		Q00352_HelpRoodRaiseANewPet.class, // Added, need html work and test.
		Q00353_PowerOfDarkness.class, // Added, need html work and test.
		Q00354_ConquestOfAlligatorIsland.class, // Added, need html work and test.
		Q00355_FamilyHonor.class, // Added, need html work and test.
		Q00356_DigUpTheSeaOfSpores.class, // Added, need html work and test.
		Q00357_WarehouseKeepersAmbition.class, // Added, need html work and test.
		Q00358_IllegitimateChildOfTheGoddess.class, // Added, need html work and test.
		Q00359_ForASleeplessDeadman.class, // Added, need html work and test.
		Q00360_PlunderTheirSupplies.class, // Added, need html work and test.
		Q00362_BardsMandolin.class, // Added, need html work and test.
		Q00363_SorrowfulSoundOfFlute.class, // Added, need html work and test.
		Q00364_JovialAccordion.class, // Added, need html work and test.
		Q00365_DevilsLegacy.class, // Added, need html work and test.
		Q00366_SilverHairedShaman.class, // Added, need html work and test.
		Q00367_ElectrifyingRecharge.class, // Added, need html work and test.
		Q00368_TrespassingIntoTheHolyGround.class, // Added, need html work and test.
		Q00369_CollectorOfJewels.class, // Added, need html work and test.
		Q00370_AnElderSowsSeeds.class, // Added, need html work and test.
		Q00371_ShrieksOfGhosts.class, // Added, need html work and test.
		Q00372_LegacyOfInsolence.class, // Added, need html work and test.
		Q00373_SupplierOfReagents.class, // Added, need html work and test.
		Q00374_WhisperOfDreamsPart1.class, // Added, need html work and test.
		Q00375_WhisperOfDreamsPart2.class, // Added, need html work and test.
		Q00376_ExplorationOfTheGiantsCavePart1.class, // Added, need html work and test.
		Q00377_ExplorationOfTheGiantsCavePart2.class, // Added, need html work and test.
		Q00378_GrandFeast.class, // Added, need html work and test.
		Q00379_FantasyWine.class, // Added, need html work and test.
		Q00380_BringOutTheFlavorOfIngredients.class, // Added, need html work and test.
		Q00381_LetsBecomeARoyalMember.class, // Added, need html work and test.
		Q00382_KailsMagicCoin.class, // Added, need html work and test.
		Q00383_TreasureHunt.class, // Added, need html work and test.
		Q00384_WarehouseKeepersPastime.class, // Added, need html work and test.
		Q00385_YokeOfThePast.class, // Added, need html work and test.
		Q00386_StolenDignity.class, // Added, need html work and test.
		Q00401_PathOfTheWarrior.class, // Added, need html work and test.
		Q00402_PathOfTheHumanKnight.class, // Added, need html work and test.
		Q00403_PathOfTheRogue.class, // Added, need html work and test.
		Q00404_PathOfTheHumanWizard.class, // Added, need html work and test.
		Q00405_PathOfTheCleric.class, // Added, need html work and test.
		Q00406_PathOfTheElvenKnight.class, // Added, need html work and test.
		Q00407_PathOfTheElvenScout.class, // Added, need html work and test.
		Q00408_PathOfTheElvenWizard.class, // Added, need html work and test.
		Q00409_PathOfTheElvenOracle.class, // Added, need html work and test.
		Q00410_PathOfThePalusKnight.class, // Added, need html work and test.
		Q00411_PathOfTheAssassin.class, // Added, need html work and test.
		Q00412_PathOfTheDarkWizard.class, // Added, need html work and test.
		Q00413_PathOfTheShillienOracle.class, // Added, need html work and test.
		Q00414_PathOfTheOrcRaider.class, // Added, need html work and test.
		Q00415_PathOfTheOrcMonk.class, // Added, need html work and test.
		Q00416_PathOfTheOrcShaman.class, // Added, need html work and test.
		Q00417_PathOfTheScavenger.class, // Added, need html work and test.
		Q00418_PathOfTheArtisan.class, // Added, need html work and test.
		Q00419_GetAPet.class, // Added, need html work and test.
		Q00420_LittleWing.class, // Added, need html work and test.
		Q00421_LittleWingsBigAdventure.class, // Added, need html work and test.
		Q00422_RepentYourSins.class, // Added, need html work and test.
		Q00426_QuestForFishingShot.class, // Added, need html work and test.
		Q00431_WeddingMarch.class, // Added, need html work and test.
		Q00432_BirthdayPartySong.class, // Added, need html work and test.
		Q00501_ProofOfClanAlliance.class, // Added, need html work and test.
		Q00503_PursuitOfClanAmbition.class, // Added, need html work and test.
		// Q00504_CompetitionForTheBanditStronghold.class,
		Q00505_BloodOffering.class, // Added, need html work and test.
		Q00508_AClansReputation.class, // Added, need html work and test.
		Q00509_AClansFame.class, // Added, need html work and test.
		Q00510_AClansPrestige.class, // Added, need html work and test.
		Q00601_WatchingEyes.class, // Added, need html work and test.
		Q00602_ShadowOfLight.class, // Added, need html work and test.
		Q00603_DaimonTheWhiteEyedPart1.class, // Added, need html work and test.
		Q00604_DaimonTheWhiteEyedPart2.class, // Added, need html work and test.
		Q00605_AllianceWithKetraOrcs.class, // Added, need html work and test.
		Q00606_BattleAgainstVarkaSilenos.class, // Added, need html work and test.
		Q00607_ProveYourCourageKetra.class, // Added, need html work and test.
		Q00608_SlayTheEnemyCommanderKetra.class, // Added, need html work and test.
		Q00609_MagicalPowerOfWaterPart1.class, // Added, need html work and test.
		Q00610_MagicalPowerOfWaterPart2.class, // Added, need html work and test.
		Q00611_AllianceWithVarkaSilenos.class, // Added, need html work and test.
		Q00612_BattleAgainstKetraOrcs.class, // Added, need html work and test.
		Q00613_ProveYourCourageVarka.class, // Added, need html work and test.
		Q00614_SlayTheEnemyCommanderVarka.class, // Added, need html work and test.
		Q00615_MagicalPowerOfFirePart1.class, // Added, need html work and test.
		Q00616_MagicalPowerOfFirePart2.class, // Added, need html work and test.
		Q00617_GatherTheFlames.class, // Added, need html work and test.
		Q00618_IntoTheFlame.class, // Added, need html work and test.
		Q00619_RelicsOfTheOldEmpire.class, // Added, need html work and test.
		Q00620_FourGoblets.class, // Added, need html work and test.
		Q00621_EggDelivery.class, // Added, need html work and test.
		Q00622_SpecialtyLiquorDelivery.class, // Added, need html work and test.
		Q00623_TheFinestFood.class, // Added, need html work and test.
		Q00624_TheFinestIngredientsPart1.class, // Added, need html work and test.
		Q00625_TheFinestIngredientsPart2.class, // Added, need html work and test.
		Q00626_ADarkTwilight.class, // Added, need html work and test.
		Q00627_HeartInSearchOfPower.class, // Added, need html work and test.
		Q00628_HuntGoldenRam.class,
		Q00629_CleanUpTheSwampOfScreams.class, // Added, need html work and test.
		Q00631_DeliciousTopChoiceMeat.class, // Added, need html work and test.
		Q00632_NecromancersRequest.class, // Added, need html work and test.
		Q00633_InTheForgottenVillage.class, // Added, need html work and test.
		Q00634_InSearchOfFragmentsOfDimension.class, // Added, need html work and test.
		Q00635_IntoTheDimensionalRift.class, // Added, need html work and test.
		Q00636_TruthBeyond.class, // Added, need html work and test.
		Q00637_ThroughOnceMore.class, // Added, need html work and test.
		Q00638_SeekersOfTheHolyGrail.class, // Added, need html work and test.
		Q00639_GuardiansOfTheHolyGrail.class, // Added, need html work and test.
		Q00640_TheZeroHour.class, // Added, need html work and test.
		Q00641_AttackSailren.class, // Added, need html work and test.
		Q00642_APowerfulPrimevalCreature.class, // Added, need html work and test.
		Q00643_RiseAndFallOfTheElrokiTribe.class, // Added, need html work and test.
		Q00644_GraveRobberAnnihilation.class, // Added, need html work and test.
		Q00645_GhostsOfBatur.class, // Added, need html work and test.
		// Q00646_SignsOfRevolt.class,
		// Q00647_InfluxOfMachines.class,
		// Q00648_AnIceMerchantsDream.class,
		// Q00649_ALooterAndARailroadMan.class,
		// Q00650_ABrokenDream.class,
		// Q00651_RunawayYouth.class,
		// Q00652_AnAgedExAdventurer.class,
		// Q00653_WildMaiden.class,
		// Q00654_JourneyToASettlement.class,
		// Q00655_AGrandPlanForTamingWildBeasts.class,
		// Q00659_IdRatherBeCollectingFairyBreath.class,
		Q00660_AidingTheFloranVillage.class, // Added, need html work and test.
		Q00661_MakingTheHarvestGroundsSafe.class, // Added, need html work and test.
		Q00662_AGameOfCards.class, // Added, need html work and test.
		Q00663_SeductiveWhispers.class, // Added, need html work and test.
		Q00688_DefeatTheElrokianRaiders.class, // Added, need html work and test.
		// Q00999_T0Tutorial.class
	};
	
	public static void main(String[] args)
	{
		for (Class<?> quest : QUESTS)
		{
			try
			{
				quest.getDeclaredConstructor().newInstance();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, QuestMasterHandler.class.getSimpleName() + ": Failed loading " + quest.getSimpleName() + ":", e);
			}
		}
	}
}
