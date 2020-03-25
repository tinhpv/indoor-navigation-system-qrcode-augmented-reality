using System;
using System.Collections.Generic;

namespace ServerAPI.Models
{
    public partial class LocationBeside
    {
        public int Id { get; set; }
        public string LocationId { get; set; }
        public string LocationBesideId { get; set; }
        public int? OrientitationId { get; set; }
        public double? Distance { get; set; }

        public virtual Location Location { get; set; }
        public virtual Location LocationBesideNavigation { get; set; }
        public virtual Orientation Orientitation { get; set; }
    }
}
