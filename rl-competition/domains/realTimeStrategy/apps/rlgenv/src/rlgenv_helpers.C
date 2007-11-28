
#include <stdlib.h>

#include "rlgenv_helpers.H"

void timing_start()
{
  system("rm /tmp/rlgenv.log");
  system("echo \"Started at\" >> /tmp/rlgenv.log");
  system("date >> /tmp/rlgenv.log"); 
}

void timing_end()
{
  system("echo \"Ended at\" >> /tmp/rlgenv.log");
  system("date >> /tmp/rlgenv.log"); 
}


void copyParms(ParameterHolder * phPtr, MiniGameParameters * mgpPtr)
{
  mgpPtr->width                        = phPtr->getIntegerParam("width");
  mgpPtr->height                       = phPtr->getIntegerParam("height");
  mgpPtr->mineral_patches              = phPtr->getIntegerParam("mineral_patches");
  
  mgpPtr->base_radius                  = phPtr->getIntegerParam("base_radius");
  mgpPtr->base_sight_range             = phPtr->getIntegerParam("base_sight_range");
  mgpPtr->base_atk_range               = phPtr->getIntegerParam("base_atk_range");
  mgpPtr->base_hp                      = phPtr->getIntegerParam("base_hp");
  mgpPtr->base_armor                   = phPtr->getIntegerParam("base_armor");  
  mgpPtr->base_cost                    = phPtr->getIntegerParam("base_cost");
  mgpPtr->base_build_time              = phPtr->getIntegerParam("base_build_time");
  mgpPtr->base_atk_value               = phPtr->getIntegerParam("base_atk_value");
  
  mgpPtr->marine_radius                = phPtr->getIntegerParam("marine_radius");
  mgpPtr->marine_sight_range           = phPtr->getIntegerParam("marine_sight_range");
  mgpPtr->marine_atk_range             = phPtr->getIntegerParam("marine_atk_range");
  mgpPtr->marine_hp                    = phPtr->getIntegerParam("marine_hp");
  mgpPtr->marine_armor                 = phPtr->getIntegerParam("marine_armor"); 
  mgpPtr->marine_max_speed             = phPtr->getIntegerParam("marine_max_speed");   
  mgpPtr->marine_cost                  = phPtr->getIntegerParam("marine_cost");
  mgpPtr->marine_training_time         = phPtr->getIntegerParam("marine_training_time");
  mgpPtr->marine_atk_value             = phPtr->getIntegerParam("marine_atk_value");
  
  mgpPtr->worker_radius                = phPtr->getIntegerParam("worker_radius");
  mgpPtr->worker_sight_range           = phPtr->getIntegerParam("worker_sight_range");
  mgpPtr->worker_atk_range             = phPtr->getIntegerParam("worker_atk_range");
  mgpPtr->worker_hp                    = phPtr->getIntegerParam("worker_hp");
  mgpPtr->worker_armor                 = phPtr->getIntegerParam("worker_armor"); 
  mgpPtr->worker_max_speed             = phPtr->getIntegerParam("worker_max_speed");   
  mgpPtr->worker_cost                  = phPtr->getIntegerParam("worker_cost");
  mgpPtr->worker_training_time         = phPtr->getIntegerParam("worker_training_time");
  mgpPtr->worker_atk_value             = phPtr->getIntegerParam("worker_atk_value");
  mgpPtr->worker_mining_time           = phPtr->getIntegerParam("worker_mining_time");  
  mgpPtr->worker_mineral_capacity      = phPtr->getIntegerParam("worker_mineral_capacity");
  
  mgpPtr->mineral_patch_radius         = phPtr->getIntegerParam("mineral_patch_radius");
  mgpPtr->mineral_patch_sight_range    = phPtr->getIntegerParam("mineral_patch_sight_range");
  mgpPtr->mineral_patch_hp             = phPtr->getIntegerParam("mineral_patch_hp");
  mgpPtr->mineral_patch_armor          = phPtr->getIntegerParam("mineral_patch_armor");
  mgpPtr->mineral_patch_capacity       = phPtr->getIntegerParam("mineral_patch_capacity");
}
