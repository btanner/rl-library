#include "MineralPatch.H"

using namespace std;

//static bool debug = true; 

static std::string type = "mineral_patch";

const std::string &MineralPatch::get_type() const { return type; }

void MineralPatch::execute()
{
  //REM("mp execute not implemented");
}

bool MineralPatch::deserialize_member(const std::string & key, const std::string & val)
{
  if (GameObj<MiniGameState>::deserialize_member(key, val))
    return true; 
  
  if      (key == "minerals_left")      minerals_left = to_int(val);
  else return false;
  
  return true;
}

void MineralPatch::serialize_members(bool allied_view, std::ostream &os) const
{
  GameObj<MiniGameState>::serialize_members(allied_view, os);
  os << ",minerals_left=" << minerals_left; 
}
