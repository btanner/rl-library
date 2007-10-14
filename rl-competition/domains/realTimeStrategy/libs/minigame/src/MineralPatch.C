#include "MineralPatch.H"

using namespace std;

static std::string type = "mineral_patch";

const std::string &MineralPatch::get_type() const { return type; }

void MineralPatch::execute()
{
  std::cout << "mp execute not implemented" << std::endl;
}

